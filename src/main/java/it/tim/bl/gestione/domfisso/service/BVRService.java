package it.tim.bl.gestione.domfisso.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import it.tim.bl.gestione.domfisso.bean.RequestBVRD;
import it.tim.bl.gestione.domfisso.bean.ResponseBVRD;
import it.tim.bl.gestione.domfisso.bean.ResponseBVRD.DatiRichiesta;
import it.tim.bl.gestione.domfisso.bean.ResponseBVRD.UtenzaFissa;
import it.tim.bl.gestione.domfisso.exception.BVRDFException;
import it.tim.bl.gestione.domfisso.repo.DomiciliazioneFissoRepository;
import it.tim.bl.gestione.domfisso.dto.DomiciliazioneFissoDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class BVRService {

	private static final Logger logger = LogManager.getLogger(BVRService.class);
	private final static String FORMAT_DATA_CONTRATTO = "yyyy-MM-ddHH:mm:ss";
	private final static String FORMAT_GIORNO = "yyyy-MM-dd";
	private final static String FORMAT_ORA = "HH:mm:ss";
	private static final String TIPO_OPERAZIONE_01 = "01";
	private static final String TIPO_OPERAZIONE_02 = "02";
	private static final String LOG = "blVisualizzaRichiestaDomfisso - ";

	@Autowired
	private DomiciliazioneFissoRepository repo;

	private final String SUBSYS = "SRTC";

	public ResponseEntity<ResponseBVRD> visualizzaRichiestaDomFisso(RequestBVRD request) throws BVRDFException {
		ResponseBVRD responseBVRD = new ResponseBVRD();
		
		String tipoOperazione = request.getVisualizzazioneRichiestaAttivazioneDomiciliazioneFisso().getTipoOperazione();
		ResponseEntity<ResponseBVRD> response = null;
		try {
			List<DomiciliazioneFissoDTO> resultQuery = new ArrayList<>();
			if(tipoOperazione.equals(TIPO_OPERAZIONE_01)) {
				resultQuery = repo.findByCodiceFiscale(request.getVisualizzazioneRichiestaAttivazioneDomiciliazioneFisso().getCf());
			}else if(tipoOperazione.equals(TIPO_OPERAZIONE_02)) {
				resultQuery = repo.findByPrefissoAndNumero(request.getVisualizzazioneRichiestaAttivazioneDomiciliazioneFisso().getUtenzaFissa().getPrefisso(), request.getVisualizzazioneRichiestaAttivazioneDomiciliazioneFisso().getUtenzaFissa().getNumero());
			}
		
			response=  ResponseEntity.ok(responseBVRD);
			response.getBody().setTipoOperazione(tipoOperazione);
			response.getBody().setEsito((request.getVisualizzazioneRichiestaAttivazioneDomiciliazioneFisso().getCf()!=null)? "01" : "02");
			response.getBody().setSubsys("NBIP");
			response.getBody().setDataOraOp(LocalDateTime.now().toString());
			//01 : richiesta OK 
			//02: errore nei controlli formali 
			//03: nessun dato trovato 
			//04: errore generico
			
			
			if(resultQuery.size()>0) {
				response.getBody().setEsito((resultQuery.size()==0)? "03" : "01");
			String dataInserimentoString = resultQuery.get(0).getDataInserimento();
	       
//			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
//	        LocalDateTime dateTime = LocalDateTime.parse(dataInserimentoString, inputFormatter);
	        
	        response.getBody().setDatiRichiesta(new DatiRichiesta() );
			response.getBody().getDatiRichiesta().setbID(resultQuery.get(0).getBid());
			response.getBody().getDatiRichiesta().setDataRichiesta(dataInserimentoString);
			response.getBody().getDatiRichiesta().setStato(resultQuery.get(0).getStato());
			response.getBody().getDatiRichiesta().setUtenzaFissa(new UtenzaFissa());
			response.getBody().getDatiRichiesta().getUtenzaFissa().setNumero(resultQuery.get(0).getNumero());
			response.getBody().getDatiRichiesta().getUtenzaFissa().setPrefisso(resultQuery.get(0).getPrefisso());
			}
		} catch (Exception e) {
			logger.error(LOG + "Errore nella ricerca del dato: ", e);
			logger.info(LOG + "Uscita dal workflow");
			throw new BVRDFException(HttpStatus.INTERNAL_SERVER_ERROR, "SDD03", "Errore generico non è stato possibile portare a termine l’esecuzione dell’operazione",
					LocalDateTime.now(), "BANKING", null, null);
		}

		return response;
	}

	
	public void validaRequest(RequestBVRD request) throws BVRDFException {	
		try {
			//se almeno uno dei campi in input è null
			if(((request.getVisualizzazioneRichiestaAttivazioneDomiciliazioneFisso().getCf()==null || request.getVisualizzazioneRichiestaAttivazioneDomiciliazioneFisso().getCf().isEmpty()) && 
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
