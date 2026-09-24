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
import it.tim.bl.gestione.domfisso.dto.VisualizzazioneRichiestaAttivazioneDomiciliazioneFisso;
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
	private static final String ESITO_OK = "01";
	private static final String ESITO_NESSUN_DATO = "03";
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
		VisualizzazioneRichiestaAttivazioneDomiciliazioneFisso richiesta = request
				.getVisualizzazioneRichiestaAttivazioneDomiciliazioneFisso();
		String tipoOperazione = richiesta.getTipoOperazione();

		try {
			List<DomiciliazioneFisso> resultQuery = cercaDomiciliazioni(richiesta, tipoOperazione);

			VisualizzaResponseDto responseBody = new VisualizzaResponseDto();
			responseBody.setTipoOperazione(tipoOperazione);
			responseBody.setSubsys("NBIP");
			responseBody.setDataOraOp(LocalDateTime.now().toString());
			//01 : richiesta OK
			//02: errore nei controlli formali
			//03: nessun dato trovato
			//04: errore generico

			if (!resultQuery.isEmpty()) {
				responseBody.setEsito(ESITO_OK);
				responseBody.setDatiRichiesta(mapDatiRichiesta(resultQuery.get(0)));
			} else {
				responseBody.setEsito(ESITO_NESSUN_DATO);
			}

			return ResponseEntity.ok(responseBody);
		} catch (DataAccessException e) {
			logger.error(LOG + "Errore nella ricerca del dato: ", e);
			throw new BVRDFException(HttpStatus.INTERNAL_SERVER_ERROR, "SDD04",
					"Errore esecuzione operazione sul database di GUP", LocalDateTime.now(), "BANKING", null, null);
		} catch (Exception e) {
			logger.error(LOG + "Errore nella ricerca del dato: ", e);
			throw new BVRDFException(HttpStatus.INTERNAL_SERVER_ERROR, "SDD03", "Errore generico non è stato possibile portare a termine l’esecuzione dell’operazione",
					LocalDateTime.now(), "BANKING", null, null);
		}
	}

	private List<DomiciliazioneFisso> cercaDomiciliazioni(
			VisualizzazioneRichiestaAttivazioneDomiciliazioneFisso richiesta, String tipoOperazione) {
		switch (tipoOperazione) {
		case TIPO_OPERAZIONE_01:
			return richiestaAttDomFissoRepository.findDomiciliazioniByCodiceFiscale(richiesta.getCf());
		case TIPO_OPERAZIONE_02:
			return domiciliazioneFissoRepository.findByPrefissoAndNumero(richiesta.getUtenzaFissa().getPrefisso(),
					richiesta.getUtenzaFissa().getNumero());
		default:
			return List.of();
		}
	}

	private DatiRichiesta mapDatiRichiesta(DomiciliazioneFisso domiciliazioneFisso) {
		DatiRichiesta dati = new DatiRichiesta();
		dati.setbID(domiciliazioneFisso.getBid());
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
