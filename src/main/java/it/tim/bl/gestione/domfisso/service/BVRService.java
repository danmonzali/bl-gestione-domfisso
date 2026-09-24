package it.tim.bl.gestione.domfisso.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import it.tim.bl.gestione.domfisso.dto.VisualizzaRequestDto;
import it.tim.bl.gestione.domfisso.dto.VisualizzaResponseDto;
import it.tim.bl.gestione.domfisso.dto.VisualizzaResponseDto.DatiRichiesta;
import it.tim.bl.gestione.domfisso.dto.VisualizzaResponseDto.UtenzaFissa;
import it.tim.bl.gestione.domfisso.entity.DomiciliazioneFisso;
import it.tim.bl.gestione.domfisso.exception.BVRDFException;
import it.tim.bl.gestione.domfisso.repo.DomiciliazioneFissoRepository;
import it.tim.bl.gestione.domfisso.repo.RichiestaAttDomFissoRepository;

@Service
public class BVRService {

	private static final Logger logger = LogManager.getLogger(BVRService.class);
	private final static String FORMAT_DATA_CONTRATTO = "yyyy-MM-ddHH:mm:ss";
	private final static String FORMAT_GIORNO = "yyyy-MM-dd";
	private final static String FORMAT_ORA = "HH:mm:ss";
	private static final String TIPO_OPERAZIONE_01 = "01";
	private static final String TIPO_OPERAZIONE_02 = "02";
	private static final String LOG = "blVisualizzaRichiestaDomfisso - ";
	private static final DateTimeFormatter FORMAT_DATA_INSERIMENTO = DateTimeFormatter
			.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

	@Autowired
	private DomiciliazioneFissoRepository domiciliazioneFissoRepository;

	@Autowired
	private RichiestaAttDomFissoRepository richiestaAttDomFissoRepository;

	private final String SUBSYS = "SRTC";

	public ResponseEntity<VisualizzaResponseDto> visualizzaRichiestaDomFisso(VisualizzaRequestDto request)
			throws BVRDFException {
		VisualizzaResponseDto responseBVRD = new VisualizzaResponseDto();

		String tipoOperazione = request.getVisualizzazioneRichiestaAttivazioneDomiciliazioneFisso().getTipoOperazione();
		ResponseEntity<VisualizzaResponseDto> response = null;
		try {
			List<DomiciliazioneFisso> resultQuery;
			if (tipoOperazione.equals(TIPO_OPERAZIONE_01)) {
				resultQuery = richiestaAttDomFissoRepository.findDomiciliazioniByCodiceFiscale(
						request.getVisualizzazioneRichiestaAttivazioneDomiciliazioneFisso().getCf());
			} else if (tipoOperazione.equals(TIPO_OPERAZIONE_02)) {
				resultQuery = domiciliazioneFissoRepository.findByPrefissoAndNumero(
						request.getVisualizzazioneRichiestaAttivazioneDomiciliazioneFisso().getUtenzaFissa().getPrefisso(),
						request.getVisualizzazioneRichiestaAttivazioneDomiciliazioneFisso().getUtenzaFissa().getNumero());
			} else {
				resultQuery = List.of();
			}

			response = ResponseEntity.ok(responseBVRD);
			response.getBody().setTipoOperazione(tipoOperazione);
			response.getBody().setEsito((request.getVisualizzazioneRichiestaAttivazioneDomiciliazioneFisso().getCf() != null) ? "01" : "02");
			response.getBody().setSubsys("NBIP");
			response.getBody().setDataOraOp(LocalDateTime.now().toString());
			//01 : richiesta OK
			//02: errore nei controlli formali
			//03: nessun dato trovato
			//04: errore generico

			if (resultQuery.size() > 0) {
				response.getBody().setEsito((resultQuery.size() == 0) ? "03" : "01");
				DomiciliazioneFisso domiciliazioneFisso = resultQuery.get(0);
				LocalDateTime dataInserimento = domiciliazioneFisso.getDataInserimento();

				response.getBody().setDatiRichiesta(new DatiRichiesta());
				response.getBody().getDatiRichiesta().setbID(domiciliazioneFisso.getBid());
				response.getBody().getDatiRichiesta()
						.setDataRichiesta(dataInserimento != null ? dataInserimento.format(FORMAT_DATA_INSERIMENTO) : null);
				response.getBody().getDatiRichiesta()
						.setStato(domiciliazioneFisso.getStato() != null ? String.valueOf(domiciliazioneFisso.getStato()) : null);
				response.getBody().getDatiRichiesta().setUtenzaFissa(new UtenzaFissa());
				response.getBody().getDatiRichiesta().getUtenzaFissa().setNumero(domiciliazioneFisso.getDatiLineaFisso().getNumero());
				response.getBody().getDatiRichiesta().getUtenzaFissa().setPrefisso(domiciliazioneFisso.getDatiLineaFisso().getPrefisso());
			}
		} catch (DataAccessException e) {
			logger.error(LOG + "Errore nella ricerca del dato: ", e);
			logger.info(LOG + "Uscita dal workflow");
			throw new BVRDFException(HttpStatus.INTERNAL_SERVER_ERROR, "SDD04",
					"Errore esecuzione operazione sul database di GUP", LocalDateTime.now(), "BANKING", null, null);
		} catch (Exception e) {
			logger.error(LOG + "Errore nella ricerca del dato: ", e);
			logger.info(LOG + "Uscita dal workflow");
			throw new BVRDFException(HttpStatus.INTERNAL_SERVER_ERROR, "SDD03", "Errore generico non è stato possibile portare a termine l’esecuzione dell’operazione",
					LocalDateTime.now(), "BANKING", null, null);
		}

		return response;
	}


	public void validaRequest(VisualizzaRequestDto request) throws BVRDFException {
		try {
			//se almeno uno dei campi in input è null
			if (((request.getVisualizzazioneRichiestaAttivazioneDomiciliazioneFisso().getCf()==null || request.getVisualizzazioneRichiestaAttivazioneDomiciliazioneFisso().getCf().isEmpty()) &&
					request.getVisualizzazioneRichiestaAttivazioneDomiciliazioneFisso().getUtenzaFissa()==null)
					|| request.getVisualizzazioneRichiestaAttivazioneDomiciliazioneFisso().getDataOraOp() ==null || request.getVisualizzazioneRichiestaAttivazioneDomiciliazioneFisso().getDataOraOp().toString().isEmpty()
					|| request.getVisualizzazioneRichiestaAttivazioneDomiciliazioneFisso().getSubsys() ==null || request.getVisualizzazioneRichiestaAttivazioneDomiciliazioneFisso().getSubsys().isEmpty()
					|| request.getVisualizzazioneRichiestaAttivazioneDomiciliazioneFisso().getTipoOperazione() ==null || request.getVisualizzazioneRichiestaAttivazioneDomiciliazioneFisso().getTipoOperazione().isEmpty()) {
				logger.info("Errore nella validazione del body della request");
				throw new BVRDFException(HttpStatus.BAD_REQUEST, "SDD06", "Errore nell’estrazione dei dati dal messaggio di input",
						LocalDateTime.now(), "BANKING", null, null);			}
			//altrimenti se tipoOperazione diverso da 01 o 02
			else if(!(request.getVisualizzazioneRichiestaAttivazioneDomiciliazioneFisso().getTipoOperazione().equals("01") || request.getVisualizzazioneRichiestaAttivazioneDomiciliazioneFisso().getTipoOperazione().equals("02")) ){
				logger.info("tipoOperazione = " + request.getVisualizzazioneRichiestaAttivazioneDomiciliazioneFisso().getTipoOperazione());
				throw new BVRDFException(HttpStatus.BAD_REQUEST, "SDD06", "Tipo operazione di input non valido",
						LocalDateTime.now(), "BANKING", null, null);
			}
		} catch (Exception e) {
			throw new BVRDFException(HttpStatus.BAD_REQUEST, "SDD06", e.getMessage(),
					LocalDateTime.now(), "BANKING", null, null);
		}

	}
}
