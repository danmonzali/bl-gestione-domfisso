package it.tim.bl.gestione.domfisso.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tim.bl.gestione.domfisso.client.AttivazioneMandatoClient;
import it.tim.bl.gestione.domfisso.dto.AttivazioneRequestDto.DatiIntestatario;
import it.tim.bl.gestione.domfisso.dto.AttivazioneRequestDto.RichiestaAttivazioneDomiciliazioneFisso;
import it.tim.bl.gestione.domfisso.dto.DatiRegistrazione;
import it.tim.bl.gestione.domfisso.entity.DatiLineaFisso;
import it.tim.bl.gestione.domfisso.entity.DomiciliazioneFisso;
import it.tim.bl.gestione.domfisso.entity.ProfiloPspSddMandato;
import it.tim.bl.gestione.domfisso.mapper.AttivazioneDomFissoMapper;
import it.tim.bl.gestione.domfisso.repo.CambioStatoDomRepository;
import it.tim.bl.gestione.domfisso.repo.DatiLineaFissoRepository;
import it.tim.bl.gestione.domfisso.repo.DomiciliazioneFissoRepository;
import it.tim.bl.gestione.domfisso.repo.ProfiloPspSddMandatoRepository;
import it.tim.bl.gestione.domfisso.util.MaskingUtil;

/**
 * Transazione di business unica, nell'ordine della SF §3.5.2. Include l'aggiornamento finale di
 * RICHIESTE_ATT_DOM_FISSO (propagazione REQUIRED). Non deve chiamare metodi di tracciamento con transazione
 * nuova: andrebbero in autodeadlock sulle righe referenziate da FK e non ancora committate.
 */
@Service
@Transactional
public class DomiciliazioneTxService {

	private static final Logger logger = LogManager.getLogger(DomiciliazioneTxService.class);

	private static final int STATO_SENZA_LINEA = 10001;
	private static final int STATO_ATTESA_MANDATO = 10002;
	private static final int STATO_ATTESA_ISAP = 10003;
	private static final int STATO_ATTESA_SIACOR = 10004;
	private static final int STATO_KO_MANDATO_IN_REVOCA = 12010;
	private static final int STATO_KO_ATTIVAZIONE_MANDATO = 12011;

	private static final String PROFILO_ATTIVO = "1";
	private static final String PROFILO_IN_ATTIVAZIONE = "2";
	private static final String PROFILO_IN_REVOCA = "4";
	private static final String PROFILO_SOSTITUITO = "5";

	@Autowired
	private DatiLineaFissoRepository datiLineaFissoRepository;

	@Autowired
	private DomiciliazioneFissoRepository domiciliazioneFissoRepository;

	@Autowired
	private ProfiloPspSddMandatoRepository profiloPspSddMandatoRepository;

	@Autowired
	private CambioStatoDomRepository cambioStatoDomRepository;

	@Autowired
	private AttivazioneMandatoClient attivazioneMandatoClient;

	@Autowired
	private ProcessoTraceService processoTraceService;

	/**
	 * @return ID_DOMICILIAZIONE della domiciliazione inserita
	 */
	public Long registra(DatiRegistrazione d) {
		RichiestaAttivazioneDomiciliazioneFisso richiesta = d.richiesta();
		boolean lineaAttiva = "Y".equals(richiesta.getUtenzaFissa().getFlagLineaAttiva());
		boolean conMandato = !"03".equals(richiesta.getTipoOperazione());

		DatiLineaFisso linea = lineaAttiva ? datiLineaFissoRepository.saveAndFlush(d.datiLinea()) : null;

		DomiciliazioneFisso dom = new DomiciliazioneFisso();
		dom.setStato(!lineaAttiva ? STATO_SENZA_LINEA : conMandato ? STATO_ATTESA_MANDATO : STATO_ATTESA_SIACOR);
		dom.setDataInserimento(LocalDateTime.now());
		dom.setDataAggiornamento(dom.getDataInserimento());
		dom.setDatiLineaFisso(linea);
		dom.setTid(d.contesto().tid());
		dom.setBid(richiesta.getBid());
		Long idDomiciliazione = domiciliazioneFissoRepository.saveAndFlush(dom).getIdDomiciliazione();

		if (lineaAttiva) {
			gestisciMandato(d, idDomiciliazione, conMandato);
		}

		processoTraceService.aggiornaProcessoEsitoFinale(d.idRichiesta(), idDomiciliazione);
		return idDomiciliazione;
	}

	private void gestisciMandato(DatiRegistrazione d, Long idDomiciliazione, boolean conMandato) {
		String cfOPiva = cfOPivaMandato(d.richiesta());
		List<ProfiloPspSddMandato> profili = profiloPspSddMandatoRepository.findByCfOPiva(cfOPiva);

		Optional<ProfiloPspSddMandato> esistente = trova(profili, PROFILO_ATTIVO)
				.or(() -> trova(profili, PROFILO_IN_ATTIVAZIONE))
				.or(() -> trova(profili, PROFILO_IN_REVOCA).filter(p -> trova(profili, PROFILO_SOSTITUITO).isEmpty()));

		if (esistente.isPresent()) {
			mandatoEsistente(idDomiciliazione, esistente.get(), !conMandato);
		} else if (conMandato) {
			attivaMandato(d, idDomiciliazione, cfOPiva);
		} else {
			// tipoOperazione 03 con mandato in stato 5: solo aggiornamento di ID_MANDATO
			trova(profili, PROFILO_SOSTITUITO).ifPresent(p -> {
				logger.info("Domiciliazione su mandato sostituito - ID_DOMICILIAZIONE = {}", idDomiciliazione);
				domiciliazioneFissoRepository.aggiornaMandato(idDomiciliazione, p.getIdMandato());
			});
		}
	}

	// SF §3.5.2.1
	private void mandatoEsistente(Long idDomiciliazione, ProfiloPspSddMandato mandato, boolean tipo03) {
		logger.info("Domiciliazione su mandato esistente - ID_DOMICILIAZIONE = {}, STATOPROFILO = {}",
				idDomiciliazione, mandato.getStatoProfilo());
		switch (mandato.getStatoProfilo()) {
		case PROFILO_ATTIVO -> {
			cambiaStato(idDomiciliazione, STATO_ATTESA_SIACOR);
			if (tipo03) {
				domiciliazioneFissoRepository.aggiornaMandato(idDomiciliazione, mandato.getIdMandato());
			}
		}
		case PROFILO_IN_ATTIVAZIONE -> cambiaStato(idDomiciliazione, STATO_ATTESA_ISAP);
		case PROFILO_IN_REVOCA -> {
			cambiaStato(idDomiciliazione, STATO_KO_MANDATO_IN_REVOCA);
			if (tipo03) {
				domiciliazioneFissoRepository.aggiornaMandato(idDomiciliazione, mandato.getIdMandato());
			}
		}
		default -> throw new IllegalStateException("STATOPROFILO non gestito: " + mandato.getStatoProfilo());
		}
	}

	// SF §3.5.2.2
	private void attivaMandato(DatiRegistrazione d, Long idDomiciliazione, String cfOPiva) {
		boolean ok = attivazioneMandatoClient.attiva(d.mandatoRequest(), d.contesto());
		if (!ok) {
			logger.error("Attivazione mandato KO - ID_DOMICILIAZIONE = {}, businessID = {}, cfOPiva = {}",
					idDomiciliazione, d.contesto().businessId(), MaskingUtil.mask(cfOPiva));
			cambiaStato(idDomiciliazione, STATO_KO_ATTIVAZIONE_MANDATO);
			return;
		}

		List<ProfiloPspSddMandato> nuovi = profiloPspSddMandatoRepository.findByCfOPiva(cfOPiva);
		logger.info("Attivazione mandato OK - ID_DOMICILIAZIONE = {}, businessID = {}", idDomiciliazione,
				d.contesto().businessId());
		cambiaStato(idDomiciliazione, STATO_ATTESA_ISAP);
		trova(nuovi, PROFILO_IN_ATTIVAZIONE)
				.ifPresent(p -> domiciliazioneFissoRepository.aggiornaMandato(idDomiciliazione, p.getIdMandato()));
	}

	private void cambiaStato(Long idDomiciliazione, int stato) {
		int esito = cambioStatoDomRepository.cambiaStato(idDomiciliazione, stato);
		if (esito != 0) {
			throw new IllegalStateException(
					"CAMBIA_STATO_DOM esito " + esito + " - ID_DOMICILIAZIONE = " + idDomiciliazione);
		}
	}

	private Optional<ProfiloPspSddMandato> trova(List<ProfiloPspSddMandato> profili, String stato) {
		return profili.stream().filter(p -> stato.equals(p.getStatoProfilo())).findFirst();
	}

	private String cfOPivaMandato(RichiestaAttivazioneDomiciliazioneFisso richiesta) {
		DatiIntestatario intestatario = richiesta.getDatiIntestatarioMandato() != null
				? richiesta.getDatiIntestatarioMandato()
				: richiesta.getDatiIntestatarioLinea();
		return AttivazioneDomFissoMapper.cfOPiva(intestatario);
	}

}
