package it.tim.bl.gestione.domfisso.mapper;

import java.time.LocalDate;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import it.tim.bl.gestione.domfisso.dto.AttivazioneMandatoRequestDto.RichiestaAttivazioneMandato;
import it.tim.bl.gestione.domfisso.dto.AttivazioneMandatoRequestDto;
import it.tim.bl.gestione.domfisso.dto.AttivazioneRequestDto.DatiIntestatario;
import it.tim.bl.gestione.domfisso.dto.AttivazioneRequestDto.PersFisica;
import it.tim.bl.gestione.domfisso.dto.AttivazioneRequestDto.PersGiur;
import it.tim.bl.gestione.domfisso.dto.AttivazioneRequestDto.RichiestaAttivazioneDomiciliazioneFisso;
import it.tim.bl.gestione.domfisso.dto.AttivazioneRequestDto.UtenzaFissa;
import it.tim.bl.gestione.domfisso.entity.DatiLineaFisso;
import it.tim.bl.gestione.domfisso.entity.RichiestaAttDomFisso;
import it.tim.bl.gestione.domfisso.service.CryptService;
import it.tim.enc.exception.EncryptDecryptException;
import it.tim.enc.pojo.EncryptPojo;

/**
 * Costruisce il dettaglio di RICHIESTE_ATT_DOM_FISSO (SF §3.6.1.2), la riga di DATI_LINEA_FISSO (SF §3.6.2)
 * e la richiesta verso bl-attivazione-mandato, cifrando i campi sensibili con la key group di BDATA.
 */
@Component
public class AttivazioneDomFissoMapper {

	@Autowired
	private CryptService cryptService;

	@Value("${key.group.bdata}")
	private String keyGroupBdata;

	/**
	 * Codice fiscale o partita IVA dell'intestatario.
	 */
	public static String cfOPiva(DatiIntestatario intestatario) {
		if (intestatario == null) {
			return null;
		}
		if (intestatario.getPersFisica() != null) {
			return intestatario.getPersFisica().getCf();
		}
		return intestatario.getPersGiur() != null ? intestatario.getPersGiur().getPiva() : null;
	}

	/**
	 * Valore 'Y'/'N' di FLAG_PERS_FISICA a partire dall'intestatario del mandato; null per tipoOperazione 03.
	 */
	public String flagPersFisica(RichiestaAttivazioneDomiciliazioneFisso r) {
		if ("03".equals(r.getTipoOperazione()) || r.getDatiIntestatarioMandato() == null) {
			return null;
		}
		return r.getDatiIntestatarioMandato().getPersFisica() != null ? "Y" : "N";
	}

	/**
	 * Valorizza il dettaglio sulla riga di processo gia' inserita; l'IBAN e' quello decifrato, ricifrato con
	 * la key group di BDATA.
	 */
	public RichiestaAttDomFisso dettaglio(RichiestaAttDomFisso p, RichiestaAttivazioneDomiciliazioneFisso r,
			String ibanInChiaro) throws EncryptDecryptException {
		DatiIntestatario linea = r.getDatiIntestatarioLinea();
		DatiIntestatario mandato = r.getDatiIntestatarioMandato();
		UtenzaFissa fissa = r.getUtenzaFissa();

		p.setBid(r.getBid());
		p.setTipoOperazione(r.getTipoOperazione());
		p.setCodoperazione(r.getTipoOperazione());
		p.setSubsys(r.getSubsys());
		p.setDataOraOp(r.getDataOraOp());
		p.setCodiceDealer(r.getCodiceDealer());
		p.setTidOrdine(r.getTidOrdine());

		p.setCfIntLinea(cfOPiva(linea));
		String nomeLinea = linea.getPersFisica() != null
				? linea.getPersFisica().getNome() + " " + linea.getPersFisica().getCognome()
				: linea.getPersGiur().getRagSociale();
		cifra(nomeLinea, p::setNomeIntLinea, p::setNomeIntLineaKeyid, p::setNomeIntLineaIv);
		cifra(comune(linea), p::setComuneIntLinea, p::setComuneIntLineaKeyid, p::setComuneIntLineaIv);
		cifra(indirizzo(linea), p::setIndirizzoIntLinea, p::setIndIntLineaKeyid, p::setIndIntLineaIv);
		cifra(linea.getIndirizzo().getCap(), p::setCapIntLinea, p::setCapIntLineaKeyid, p::setCapIntLineaIv);

		p.setPrefLinea(fissa.getPrefisso());
		p.setNumLinea(fissa.getNumero());
		p.setFlagLineaAttiva(fissa.getFlagLineaAttiva());
		p.setDataAttivazioneLinea(StringUtils.isNotBlank(fissa.getDataAttivazioneLinea())
				? LocalDate.parse(fissa.getDataAttivazioneLinea())
				: null);

		p.setFlagPersFisica(flagPersFisica(r));
		if (mandato != null) {
			dettaglioMandato(p, mandato);
		}

		if (r.getDatiISAP() != null) {
			p.setMarcaggiocliente(r.getDatiISAP().getMarcaggioCliente());
			p.setCodiceterritoriale(r.getDatiISAP().getCodiceTerritoriale());
			p.setProvenienzasid(r.getDatiISAP().getProvenienzaSID());
			p.setProfilocontrattuale(r.getDatiISAP().getProfiloContrattuale());
		}

		if (r.getDatiContoCorrenteAttivazioneDomiciliazione() != null) {
			p.setIdmandato(r.getDatiContoCorrenteAttivazioneDomiciliazione().getIdMandato());
			cifra(r.getDatiContoCorrenteAttivazioneDomiciliazione().getCodiceAutorizzativo(), p::setCodiceautorizzativo,
					p::setCodiceautorizzativoKeyid, p::setCodiceautorizzativoIv);
		}
		cifra(ibanInChiaro, p::setIban, p::setIbanKeyid, p::setIbanIv);
		return p;
	}

	private void dettaglioMandato(RichiestaAttDomFisso p, DatiIntestatario mandato) throws EncryptDecryptException {
		p.setCfIntMandato(cfOPiva(mandato));
		if (mandato.getPersFisica() != null) {
			cifra(mandato.getPersFisica().getNome(), p::setNomeIntMand, p::setNomeIntMandKeyid, p::setNomeIntMandIv);
			cifra(mandato.getPersFisica().getCognome(), p::setCognomeIntMand, p::setCognomeIntMandKeyid,
					p::setCognomeIntMandIv);
		} else if (mandato.getPersGiur() != null) {
			cifra(mandato.getPersGiur().getRagSociale(), p::setCognomeIntMand, p::setCognomeIntMandKeyid,
					p::setCognomeIntMandIv);
		}
		if (mandato.getUtenzaMobile() != null) {
			p.setPrefIntMandato(mandato.getUtenzaMobile().getPrefisso());
			p.setNumIntMandato(mandato.getUtenzaMobile().getNumero());
		}
		if (mandato.getIndirizzo() != null) {
			var i = mandato.getIndirizzo();
			cifra(i.getDescComune(), p::setComuneIntMand, p::setComuneIntMandKeyid, p::setComuneIntMandIv);
			cifra(i.getSiglaProvincia(), p::setProvinciaIntMand, p::setProvinciaIntMandKeyid,
					p::setProvinciaIntMandIv);
			cifra(i.getDescIndirizzo(), p::setIndirizzoIntMand, p::setIndirizzoIntMandKeyid,
					p::setIndirizzoIntMandIv);
			cifra(i.getCap(), p::setCapIntMand, p::setCapIntMandKeyid, p::setCapIntMandIv);
			// PART_TOP_INT_MAND e CIVICO_INT_MAND non hanno colonne _KEYID/_IV: restano in chiaro
			p.setPartTopIntMand(i.getPartToponomastica());
			p.setCivicoIntMand(i.getNumCivico());
		}
	}

	/**
	 * Riga di DATI_LINEA_FISSO: riusa i valori cifrati gia' calcolati per il dettaglio della richiesta.
	 */
	public DatiLineaFisso datiLinea(RichiestaAttDomFisso p) {
		DatiLineaFisso l = new DatiLineaFisso();
		l.setCfIntestatario(p.getCfIntLinea());
		l.setPrefisso(p.getPrefLinea());
		l.setNumero(p.getNumLinea());
		l.setDataAttivazioneLinea(p.getDataAttivazioneLinea());
		l.setComuneProv(p.getComuneIntLinea());
		l.setComuneProvKeyid(p.getComuneIntLineaKeyid());
		l.setComuneProvIv(p.getComuneIntLineaIv());
		l.setIndirizzo(p.getIndirizzoIntLinea());
		l.setIndirizzoKeyid(p.getIndIntLineaKeyid());
		l.setIndirizzoIv(p.getIndIntLineaIv());
		l.setCap(p.getCapIntLinea());
		l.setCapKeyid(p.getCapIntLineaKeyid());
		l.setCapIv(p.getCapIntLineaIv());
		l.setNomeIntestatario(p.getNomeIntLinea());
		l.setNomeKeyid(p.getNomeIntLineaKeyid());
		l.setNomeIv(p.getNomeIntLineaIv());
		return l;
	}

	/**
	 * Richiesta per bl-attivazione-mandato (tipoOperazione 01/02) con l'IBAN cifrato ricevuto, non ricifrato.
	 */
	public AttivazioneMandatoRequestDto richiestaMandato(RichiestaAttivazioneDomiciliazioneFisso r,
			String flagPersFisica) {
		DatiIntestatario mandato = r.getDatiIntestatarioMandato();
		RichiestaAttivazioneMandato m = new RichiestaAttivazioneMandato();
		m.setTipoOperazione(r.getTipoOperazione());
		m.setSubsys(r.getSubsys());
		m.setDataOraOp(r.getDataOraOp());
		m.setCodiceDealer(r.getCodiceDealer());
		m.setTidOrdine(r.getTidOrdine());
		if ("Y".equals(flagPersFisica)) {
			m.setIsPersFisica("Y");
			PersFisica pf = new PersFisica();
			pf.setCf(mandato.getPersFisica().getCf());
			pf.setNome(mandato.getPersFisica().getNome());
			pf.setCognome(mandato.getPersFisica().getCognome());
			m.setPersFisica(pf);
		} else {
			m.setIsPersGiur("Y");
			PersGiur pg = new PersGiur();
			pg.setPiva(mandato.getPersGiur().getPiva());
			pg.setRagSociale(mandato.getPersGiur().getRagSociale());
			m.setPersGiur(pg);
		}
		m.setIndirizzo(mandato.getIndirizzo());
		var conto = new AttivazioneMandatoRequestDto.DatiContoCorrente();
		conto.setIban(r.getDatiContoCorrenteAttivazioneDomiciliazione().getIban());
		conto.setCodiceAutorizzativo(r.getDatiContoCorrenteAttivazioneDomiciliazione().getCodiceAutorizzativo());
		m.setDatiContoCorrente(conto);
		m.setUtenzaMobile(mandato.getUtenzaMobile());
		m.setDatiISAP(r.getDatiISAP());
		AttivazioneMandatoRequestDto dto = new AttivazioneMandatoRequestDto();
		dto.setRichiestaAttivazioneMandato(m);
		return dto;
	}

	private String comune(DatiIntestatario d) {
		return d.getIndirizzo().getDescComune() + " (" + d.getIndirizzo().getSiglaProvincia() + ")";
	}

	private String indirizzo(DatiIntestatario d) {
		var i = d.getIndirizzo();
		return i.getPartToponomastica() + " " + i.getDescIndirizzo() + ", " + i.getNumCivico();
	}

	private void cifra(String clearText, Consumer<String> valore, Consumer<String> keyId, Consumer<String> iv)
			throws EncryptDecryptException {
		if (StringUtils.isEmpty(clearText)) {
			return;
		}
		EncryptPojo cifrato = cifra(keyGroupBdata, clearText);
		valore.accept(cifrato.getChiperText());
		keyId.accept(cifrato.getKeyId());
		iv.accept(cifrato.getIv());
	}

	private EncryptPojo cifra(String keyGroup, String clearText) throws EncryptDecryptException {
		EncryptPojo ep = new EncryptPojo();
		ep.setClearText(clearText);
		ep.setKeyGroup(keyGroup);
		return cryptService.encrypt(ep);
	}

}
