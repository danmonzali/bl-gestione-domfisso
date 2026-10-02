package it.tim.bl.gestione.domfisso.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import it.tim.bl.gestione.domfisso.dto.AttivazioneRequestDto.DatiContoCorrente;
import it.tim.bl.gestione.domfisso.dto.AttivazioneRequestDto.DatiIntestatario;
import it.tim.bl.gestione.domfisso.dto.AttivazioneRequestDto.DatiIsap;
import it.tim.bl.gestione.domfisso.dto.AttivazioneRequestDto.Indirizzo;
import it.tim.bl.gestione.domfisso.dto.AttivazioneRequestDto.PersFisica;
import it.tim.bl.gestione.domfisso.dto.AttivazioneRequestDto.PersGiur;
import it.tim.bl.gestione.domfisso.dto.AttivazioneRequestDto.RichiestaAttivazioneDomiciliazioneFisso;
import it.tim.bl.gestione.domfisso.dto.AttivazioneRequestDto.UtenzaFissa;
import it.tim.bl.gestione.domfisso.dto.AttivazioneRequestDto.UtenzaMobile;

/**
 * Controlli formali (SF §3.3) e controllo sui dati dell'utenza fissa (esito 015). Classe pura, senza dipendenze:
 * restituisce il codice di esito del primo controllo fallito.
 */
@Component
public class AttivazioneDomFissoValidator {

	static final String TIPO_01 = "01";
	static final String TIPO_02 = "02";
	static final String TIPO_03 = "03";

	private static final Set<String> SUBSYS_AMMESSI = Set.of("DBSSDealer", "DBSSHOPAPP", "DBSSCC", "DBSSWEBCdC",
			"DBSSWEB", "MYTIMAPP", "MYTIMWEB");
	private static final DateTimeFormatter FORMAT_DATA_ORA = DateTimeFormatter
			.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSS").withResolverStyle(ResolverStyle.STRICT);
	private static final DateTimeFormatter FORMAT_DATA = DateTimeFormatter.ofPattern("uuuu-MM-dd")
			.withResolverStyle(ResolverStyle.STRICT);

	public Optional<String> controlliFormali(RichiestaAttivazioneDomiciliazioneFisso r) {
		String tipo = r.getTipoOperazione();
		boolean conMandato = TIPO_01.equals(tipo) || TIPO_02.equals(tipo);

		if (!conMandato && !TIPO_03.equals(tipo)) {
			return Optional.of("002");
		}
		if (!valorizzato(r.getSubsys(), 10) || !SUBSYS_AMMESSI.contains(r.getSubsys())) {
			return Optional.of("003");
		}
		if (!valorizzato(r.getCodiceDealer(), 10)) {
			return Optional.of("004");
		}
		if (!valorizzato(r.getTidOrdine(), 30)) {
			return Optional.of("010");
		}
		if (!valorizzato(r.getBid(), 30)) {
			return Optional.of("017");
		}
		if (!dataOraValida(r.getDataOraOp())) {
			return Optional.of("018");
		}

		DatiIntestatario linea = r.getDatiIntestatarioLinea();
		if (linea == null || !anagraficaValida(linea)) {
			return Optional.of("005");
		}
		if (!indirizzoValido(linea.getIndirizzo())) {
			return Optional.of("006");
		}

		if (conMandato) {
			DatiIntestatario mandato = r.getDatiIntestatarioMandato();
			if (mandato == null || !anagraficaValida(mandato)) {
				return Optional.of("007");
			}
			if (!indirizzoValido(mandato.getIndirizzo())) {
				return Optional.of("008");
			}
			if (!utenzaMobileValida(mandato.getUtenzaMobile())) {
				return Optional.of("009");
			}
		}

		Optional<String> conto = controlliContoCorrente(tipo, r.getDatiContoCorrenteAttivazioneDomiciliazione());
		if (conto.isPresent()) {
			return conto;
		}

		UtenzaFissa fissa = r.getUtenzaFissa();
		if (fissa == null || !("Y".equals(fissa.getFlagLineaAttiva()) || "N".equals(fissa.getFlagLineaAttiva()))) {
			return Optional.of("020");
		}

		if (conMandato && !datiIsapValidi(r.getDatiISAP())) {
			return Optional.of("016");
		}

		if ("Y".equals(fissa.getFlagLineaAttiva()) && !utenzaFissaAttivaValida(fissa)) {
			return Optional.of("015");
		}
		return Optional.empty();
	}

	private Optional<String> controlliContoCorrente(String tipo, DatiContoCorrente conto) {
		String iban = conto != null ? conto.getIban() : null;
		String codice = conto != null ? conto.getCodiceAutorizzativo() : null;
		String idMandato = conto != null ? conto.getIdMandato() : null;

		if (TIPO_01.equals(tipo) && !valorizzato(iban, 100)) {
			return Optional.of("011");
		}
		if (TIPO_02.equals(tipo) && !valorizzato(codice, 20)) {
			return Optional.of("012");
		}
		if (TIPO_03.equals(tipo)) {
			if (StringUtils.isNotEmpty(iban)) {
				return Optional.of("011");
			}
			if (StringUtils.isNotEmpty(codice)) {
				return Optional.of("012");
			}
			if (!valorizzato(idMandato, 24)) {
				return Optional.of("013");
			}
		}
		return Optional.empty();
	}

	private boolean anagraficaValida(DatiIntestatario d) {
		PersFisica pf = d.getPersFisica();
		PersGiur pg = d.getPersGiur();
		if (pf != null && pg == null) {
			return valorizzato(pf.getCf(), 16) && valorizzato(pf.getNome(), 100) && valorizzato(pf.getCognome(), 100);
		}
		if (pg != null && pf == null) {
			return valorizzato(pg.getPiva(), 11) && valorizzato(pg.getRagSociale(), 70);
		}
		return false;
	}

	private boolean indirizzoValido(Indirizzo i) {
		return i != null && valorizzato(i.getDescComune(), 40) && valorizzato(i.getSiglaProvincia(), 3)
				&& valorizzato(i.getPartToponomastica(), 21) && valorizzato(i.getDescIndirizzo(), 65)
				&& valorizzato(i.getNumCivico(), 10) && valorizzato(i.getCap(), 5);
	}

	private boolean utenzaMobileValida(UtenzaMobile u) {
		return u != null && valorizzato(u.getPrefisso(), 3) && valorizzato(u.getNumero(), 7);
	}

	private boolean datiIsapValidi(DatiIsap d) {
		return d != null && "S".equals(d.getMarcaggioCliente()) && "0000".equals(d.getCodiceTerritoriale())
				&& "9".equals(d.getProvenienzaSID()) && "00000".equals(d.getCausaleOperazione())
				&& "BRP".equals(d.getProfiloContrattuale());
	}

	private boolean utenzaFissaAttivaValida(UtenzaFissa u) {
		return valorizzato(u.getPrefisso(), 4) && valorizzato(u.getNumero(), 8)
				&& dataValida(u.getDataAttivazioneLinea());
	}

	private boolean valorizzato(String valore, int maxLen) {
		return StringUtils.isNotBlank(valore) && valore.length() <= maxLen;
	}

	private boolean dataOraValida(String valore) {
		if (StringUtils.isBlank(valore)) {
			return false;
		}
		try {
			LocalDateTime.parse(valore, FORMAT_DATA_ORA);
			return true;
		} catch (DateTimeParseException e) {
			return false;
		}
	}

	private boolean dataValida(String valore) {
		if (StringUtils.isBlank(valore)) {
			return false;
		}
		try {
			LocalDate.parse(valore, FORMAT_DATA);
			return true;
		} catch (DateTimeParseException e) {
			return false;
		}
	}

}
