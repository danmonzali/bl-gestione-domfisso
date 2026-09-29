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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Service
public class BVRService {

	private static final Logger logger = LogManager.getLogger(BVRService.class);
	private static final String TIPO_OPERAZIONE_01 = "01";
	private static final String TIPO_OPERAZIONE_02 = "02";
	private static final String ESITO_OK = "01";
	private static final String ESITO_KO = "02";
	private static final String ESITO_NESSUN_DATO = "03";
	private static final String SUBSYS_RISPOSTA = "NBIP";
	private static final Set<String> SUBSYS_AMMESSI = Set.of("DBSSDealer", "DBSSHOPAPP", "DBSSCC", "DBSSWEBCdC",
			"DBSSWEB", "MYTIMAPP", "MYTIMWEB");
	private static final int MAX_LEN_SUBSYS = 10;
	private static final int MAX_LEN_CF = 16;
	private static final int MAX_LEN_PREFISSO = 4;
	private static final int MAX_LEN_NUMERO = 8;
	private static final ZoneId ITALY_ZONE_ID = ZoneId.of("Europe/Rome");
	private static final DateTimeFormatter FORMAT_DATA_ORA = DateTimeFormatter
			.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

	@Autowired
	private DomiciliazioneFissoRepository domiciliazioneFissoRepository;

	@Autowired
	private RichiestaAttDomFissoRepository richiestaAttDomFissoRepository;


	public VisualizzaResponseDto visualizzaRichiestaDomFisso(VisualizzaRequestDto request) {
		try {
			// Nessun campo della request va letto prima della validazione
			if (!this.validaRequest(request)) {
				VisualizzazioneRichiestaAttivazioneDomiciliazioneFisso richiestaNonValida = request != null
						? request.getVisualizzazioneRichiestaAttivazioneDomiciliazioneFisso()
						: null;
				VisualizzaResponseDto responseBody = createResponseBody(
						richiestaNonValida != null ? richiestaNonValida.getTipoOperazione() : null);
				responseBody.setEsito(ESITO_KO);
				return responseBody;
			}

			VisualizzazioneRichiestaAttivazioneDomiciliazioneFisso richiesta = request
					.getVisualizzazioneRichiestaAttivazioneDomiciliazioneFisso();
			String tipoOperazione = richiesta.getTipoOperazione();

			List<DomiciliazioneFisso> resultQuery = cercaDomiciliazioni(richiesta, tipoOperazione);

			VisualizzaResponseDto responseBody = createResponseBody(tipoOperazione);

			if (!resultQuery.isEmpty()) {
				responseBody.setEsito(ESITO_OK);
				responseBody.setDatiRichiesta(mapDatiRichiesta(resultQuery.getFirst()));
			} else {
				responseBody.setEsito(ESITO_NESSUN_DATO);
			}

			return responseBody;
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
		responseBody.setSubsys(SUBSYS_RISPOSTA);
		responseBody.setDataOraRisposta(LocalDateTime.now(ITALY_ZONE_ID).format(FORMAT_DATA_ORA));
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
		dati.setDataRichiesta(formatDataOra(domiciliazioneFisso.getDataInserimento()));
		dati.setStato(domiciliazioneFisso.getStato() != null ? String.valueOf(domiciliazioneFisso.getStato()) : null);

		if (domiciliazioneFisso.getDatiLineaFisso() != null) {
			UtenzaFissa utenzaFissa = new UtenzaFissa();
			utenzaFissa.setNumero(domiciliazioneFisso.getDatiLineaFisso().getNumero());
			utenzaFissa.setPrefisso(domiciliazioneFisso.getDatiLineaFisso().getPrefisso());
			dati.setUtenzaFissa(utenzaFissa);
		}
		return dati;
	}

	private static String formatDataOra(LocalDateTime dataOra) {
		return dataOra != null ? dataOra.format(FORMAT_DATA_ORA) : null;
	}


	public Boolean validaRequest(VisualizzaRequestDto request) {
		VisualizzazioneRichiestaAttivazioneDomiciliazioneFisso visualizzazioneRichiesta = request != null
				? request.getVisualizzazioneRichiestaAttivazioneDomiciliazioneFisso()
				: null;

		if (visualizzazioneRichiesta == null) {
			logger.info("Errore nella validazione del body della request: oggetto visualizzazioneRichiestaAttivazioneDomiciliazioneFisso assente");
			return false;
		}

		// tipoOperazione obbligatorio e ammesso solo 01 o 02
		String tipoOperazione = visualizzazioneRichiesta.getTipoOperazione();
		if (!TIPO_OPERAZIONE_01.equals(tipoOperazione) && !TIPO_OPERAZIONE_02.equals(tipoOperazione)) {
			logger.info("Errore nella validazione del body della request: tipoOperazione = {}", tipoOperazione);
			return false;
		}

		// subsys obbligatorio, max 10 caratteri e appartenente al dominio ammesso
		String subsys = visualizzazioneRichiesta.getSubsys();
		if (StringUtils.isBlank(subsys) || subsys.length() > MAX_LEN_SUBSYS || !SUBSYS_AMMESSI.contains(subsys)) {
			logger.info("Errore nella validazione del body della request: subsys = {}", subsys);
			return false;
		}

		if (visualizzazioneRichiesta.getDataOraOp() == null) {
			logger.info("Errore nella validazione del body della request: dataOraOp assente");
			return false;
		}

		// Lunghezze massime dei campi valorizzati
		String cf = visualizzazioneRichiesta.getCf();
		VisualizzazioneRichiestaAttivazioneDomiciliazioneFisso.UtenzaFissa utenzaFissa = visualizzazioneRichiesta
				.getUtenzaFissa();
		if (StringUtils.length(cf) > MAX_LEN_CF
				|| (utenzaFissa != null && (StringUtils.length(utenzaFissa.getPrefisso()) > MAX_LEN_PREFISSO
						|| StringUtils.length(utenzaFissa.getNumero()) > MAX_LEN_NUMERO))) {
			logger.info("Errore nella validazione del body della request: lunghezza massima dei campi superata");
			return false;
		}

		// cf obbligatorio solo per tipoOperazione 01
		if (TIPO_OPERAZIONE_01.equals(tipoOperazione) && StringUtils.isBlank(cf)) {
			logger.info("Errore nella validazione del body della request: cf obbligatorio per tipoOperazione 01");
			return false;
		}

		// utenzaFissa (prefisso e numero) obbligatoria solo per tipoOperazione 02
		if (TIPO_OPERAZIONE_02.equals(tipoOperazione) && (utenzaFissa == null
				|| StringUtils.isBlank(utenzaFissa.getPrefisso()) || StringUtils.isBlank(utenzaFissa.getNumero()))) {
			logger.info("Errore nella validazione del body della request: utenzaFissa obbligatoria per tipoOperazione 02");
			return false;
		}

		return true;
	}
}
