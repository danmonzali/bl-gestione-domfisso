package it.tim.bl.gestione.domfisso.service;

import it.tim.bl.gestione.domfisso.dto.VisualizzaRequestDto;
import it.tim.bl.gestione.domfisso.dto.VisualizzaResponseDto;
import it.tim.bl.gestione.domfisso.dto.VisualizzaResponseDto.DatiRichiesta;
import it.tim.bl.gestione.domfisso.dto.VisualizzaResponseDto.UtenzaFissa;
import it.tim.bl.gestione.domfisso.dto.VisualizzazioneRichiestaAttivazioneDomiciliazioneFisso;
import it.tim.bl.gestione.domfisso.entity.DomiciliazioneFisso;
import it.tim.bl.gestione.domfisso.exception.ISEExceptionReturn;
import it.tim.bl.gestione.domfisso.repo.DomiciliazioneFissoRepository;
import it.tim.bl.gestione.domfisso.repo.RichiestaAttDomFissoRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
public class BVRService {

	private static final Logger logger = LogManager.getLogger(BVRService.class);
	private static final String TIPO_OPERAZIONE_01 = "01";
	private static final String TIPO_OPERAZIONE_02 = "02";
	private static final String ESITO_OK = "01";
	private static final String ESITO_KO = "02";
	private static final String ESITO_NESSUN_DATO = "03";
	private static final DateTimeFormatter FORMAT_DATA_INSERIMENTO = DateTimeFormatter
			.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

	@Autowired
	private DomiciliazioneFissoRepository domiciliazioneFissoRepository;

	@Autowired
	private RichiestaAttDomFissoRepository richiestaAttDomFissoRepository;


	public ResponseEntity<VisualizzaResponseDto> visualizzaRichiestaDomFisso(VisualizzaRequestDto request) {
		try {
			VisualizzazioneRichiestaAttivazioneDomiciliazioneFisso richiesta = request
					.getVisualizzazioneRichiestaAttivazioneDomiciliazioneFisso();
			String tipoOperazione = richiesta.getTipoOperazione();
			VisualizzaResponseDto responseBody = null;

			Boolean isValid = this.validaRequest(request);

			// Se la richiesta non è valida, restituisci un esito KO
			if (!isValid) {
				responseBody = createResponseBody(tipoOperazione);
				responseBody.setEsito(ESITO_KO);
				return ResponseEntity.ok(responseBody);
			}

			List<DomiciliazioneFisso> resultQuery = cercaDomiciliazioni(richiesta, tipoOperazione);

			responseBody = createResponseBody(tipoOperazione);

			if (!resultQuery.isEmpty()) {
				responseBody.setEsito(ESITO_OK);
				responseBody.setDatiRichiesta(mapDatiRichiesta(resultQuery.getFirst()));
			} else {
				responseBody.setEsito(ESITO_NESSUN_DATO);
			}

			return ResponseEntity.ok(responseBody);
		} catch (DataAccessException e) {
			logger.error("Errore nell’esecuzione dell’operazione sul database di GUP: ", e);
			throw ISEExceptionReturn.CODICE_674();
		} catch (Exception e) {
			logger.error("Errore generico durante la visualizzazione richiesta domfisso: ", e);
			throw ISEExceptionReturn.CODICE_674();
		}
	}

	private VisualizzaResponseDto createResponseBody(String tipoOperazione) {
		VisualizzaResponseDto responseBody = new VisualizzaResponseDto();
		responseBody.setTipoOperazione(tipoOperazione);
		responseBody.setSubsys("NBIP");
		responseBody.setDataOraRisposta(LocalDateTime.now().toString());
		return responseBody;
	}

	private List<DomiciliazioneFisso> cercaDomiciliazioni(
			VisualizzazioneRichiestaAttivazioneDomiciliazioneFisso richiesta, String tipoOperazione) {
        return switch (tipoOperazione) {
            case TIPO_OPERAZIONE_01 ->
                    richiestaAttDomFissoRepository.findDomiciliazioniByCodiceFiscale(richiesta.getCf());
            case TIPO_OPERAZIONE_02 ->
                    domiciliazioneFissoRepository.findByPrefissoAndNumero(richiesta.getUtenzaFissa().getPrefisso(),
                            richiesta.getUtenzaFissa().getNumero());
            default -> List.of();
        };
	}

	private DatiRichiesta mapDatiRichiesta(DomiciliazioneFisso domiciliazioneFisso) {
		DatiRichiesta dati = new DatiRichiesta();
		dati.setBid(domiciliazioneFisso.getBid());
		dati.setDataRichiesta(formatDataInserimento(domiciliazioneFisso.getDataInserimento()));
		dati.setStato(domiciliazioneFisso.getStato() != null ? String.valueOf(domiciliazioneFisso.getStato()) : null);

		UtenzaFissa utenzaFissa = new UtenzaFissa();
		utenzaFissa.setNumero(domiciliazioneFisso.getDatiLineaFisso().getNumero());
		utenzaFissa.setPrefisso(domiciliazioneFisso.getDatiLineaFisso().getPrefisso());
		dati.setUtenzaFissa(utenzaFissa);
		return dati;
	}

	private static String formatDataInserimento(LocalDateTime dataInserimento) {
		return dataInserimento != null ? dataInserimento.format(FORMAT_DATA_INSERIMENTO) : null;
	}


	public Boolean validaRequest(VisualizzaRequestDto request) {
		VisualizzazioneRichiestaAttivazioneDomiciliazioneFisso visualizzazioneRichiesta = request.getVisualizzazioneRichiestaAttivazioneDomiciliazioneFisso();

		if (visualizzazioneRichiesta == null) {
			logger.info("Errore nella validazione del body della request");
			return false;
		}

		//se tipoOperazione diverso da 01 o 02
		String tipoOperazione = visualizzazioneRichiesta.getTipoOperazione();
		if(!(tipoOperazione.equals("01") || tipoOperazione.equals("02")) ){
			logger.info("tipoOperazione = {}", tipoOperazione);
			return false;
		}

		//se almeno uno dei campi in input è null
		if (((StringUtils.isBlank(visualizzazioneRichiesta.getCf()) && visualizzazioneRichiesta.getUtenzaFissa() == null)
				|| Objects.isNull(visualizzazioneRichiesta.getDataOraOp())
				|| StringUtils.isBlank(visualizzazioneRichiesta.getSubsys())
				|| StringUtils.isBlank(visualizzazioneRichiesta.getTipoOperazione()))) {
			logger.info("Errore nella validazione del body della request");
			return false;
		}

		return true;
	}
}
