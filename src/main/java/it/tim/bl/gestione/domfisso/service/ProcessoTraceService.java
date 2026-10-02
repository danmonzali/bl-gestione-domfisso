package it.tim.bl.gestione.domfisso.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import it.tim.bl.gestione.domfisso.dto.ContestoRichiesta;
import it.tim.bl.gestione.domfisso.entity.RichiestaAttDomFisso;
import it.tim.bl.gestione.domfisso.repo.RichiestaAttDomFissoRepository;

/**
 * Unico punto di scrittura su RICHIESTE_ATT_DOM_FISSO.
 * <p>
 * Le scritture di tracciamento usano una transazione propria, cosi' non vengono annullate dal rollback
 * delle scritture di business; vanno chiamate solo dall'orchestratore, mai da dentro la transazione di
 * business ({@code DomiciliazioneTxService}).
 * </p>
 */
@Service
public class ProcessoTraceService {

	private static final int MAX_LEN_TID = 25;

	@Autowired
	private RichiestaAttDomFissoRepository richiestaAttDomFissoRepository;

	/**
	 * Genera il TID della richiesta: oggi e' il {@code businessID} troncato a 25 caratteri.
	 * Punto unico da sostituire con l'algoritmo standard GUP.
	 */
	public String generaTid(String businessId) {
		if (businessId == null || businessId.length() <= MAX_LEN_TID) {
			return businessId;
		}
		return businessId.substring(0, MAX_LEN_TID);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public RichiestaAttDomFisso inserimentoProcesso(ContestoRichiesta contesto) {
		RichiestaAttDomFisso richiesta = new RichiestaAttDomFisso();
		richiesta.setTidRichiestaAtt(generaTid(contesto.businessId()));
		richiesta.setDataRichiesta(LocalDateTime.now());
		richiesta.setSessionId(contesto.sessionId());
		richiesta.setBusinessId(contesto.businessId());
		richiesta.setTransactionId(contesto.transactionId());
		richiesta.setMessageId(contesto.messageId());
		richiesta.setSourcesystem(contesto.sourceSystem());
		richiesta.setChannel(contesto.channel());
		return richiestaAttDomFissoRepository.saveAndFlush(richiesta);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void aggiornaProcesso(Long idRichiesta, String esito) {
		RichiestaAttDomFisso richiesta = richiestaAttDomFissoRepository.findById(idRichiesta).orElseThrow();
		richiesta.setEsito(esito);
		richiestaAttDomFissoRepository.save(richiesta);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public RichiestaAttDomFisso salvaProcesso(RichiestaAttDomFisso richiesta) {
		return richiestaAttDomFissoRepository.saveAndFlush(richiesta);
	}

	/**
	 * Chiude la richiesta con {@code ID_DOMICILIAZIONE} ed esito {@code 000}.
	 * <p>
	 * Deve unirsi alla transazione di business del chiamante: con una transazione nuova si avrebbe un
	 * autodeadlock Oracle sul lock della riga di domiciliazione ancora non committata, referenziata da FK.
	 * </p>
	 */
	@Transactional
	public void aggiornaProcessoEsitoFinale(Long idRichiesta, Long idDomiciliazione) {
		RichiestaAttDomFisso richiesta = richiestaAttDomFissoRepository.findById(idRichiesta).orElseThrow();
		richiesta.setIdDomiciliazione(idDomiciliazione);
		richiesta.setEsito("000");
		richiestaAttDomFissoRepository.save(richiesta);
	}

}
