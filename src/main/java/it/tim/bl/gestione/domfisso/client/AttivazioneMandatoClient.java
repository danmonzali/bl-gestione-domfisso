package it.tim.bl.gestione.domfisso.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import it.tim.bl.gestione.domfisso.dto.AttivazioneMandatoRequestDto;
import it.tim.bl.gestione.domfisso.dto.AttivazioneMandatoResponseDto;
import it.tim.bl.gestione.domfisso.dto.ContestoRichiesta;

/**
 * Client REST di bl-attivazione-mandato. Non solleva eccezioni: ogni anomalia (connessione, timeout, HTTP diverso
 * da 200, esito diverso da 1) restituisce {@code false}, e il chiamante imposta lo stato di KO.
 */
@Component
public class AttivazioneMandatoClient {

	private static final Logger logger = LogManager.getLogger(AttivazioneMandatoClient.class);
	private static final int ESITO_OK = 1;

	private final RestClient restClient;
	private final String sourceSystem;
	private final String channel;

	public AttivazioneMandatoClient(@Value("${bl.attivazione.mandato.url}") String url,
			@Value("${bl.attivazione.mandato.connect-timeout:5000}") int connectTimeout,
			@Value("${bl.attivazione.mandato.read-timeout:30000}") int readTimeout,
			@Value("${source.system}") String sourceSystem, @Value("${channel}") String channel) {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(connectTimeout);
		factory.setReadTimeout(readTimeout);
		this.restClient = RestClient.builder().baseUrl(url).requestFactory(factory).build();
		this.sourceSystem = sourceSystem;
		this.channel = channel;
	}

	/**
	 * @return {@code true} solo con HTTP 200 ed {@code esito} = 1
	 */
	public boolean attiva(AttivazioneMandatoRequestDto request, ContestoRichiesta contesto) {
		try {
			ResponseEntity<AttivazioneMandatoResponseDto> response = restClient.post()
					.contentType(MediaType.APPLICATION_JSON)
					.header("sourceSystem", sourceSystem)
					.header("channel", channel)
					.header("interactionDate-Date", contesto.interactionDateDate())
					.header("interactionDate-Time", contesto.interactionDateTime())
					.header("sessionID", contesto.sessionId())
					.header("businessID", contesto.businessId())
					.header("transactionID", contesto.transactionId())
					.header("messageID", contesto.messageId())
					.body(request)
					.retrieve()
					.toEntity(AttivazioneMandatoResponseDto.class);

			AttivazioneMandatoResponseDto body = response.getBody();
			boolean ok = response.getStatusCode().value() == 200 && body != null
					&& body.getEsito() != null && body.getEsito() == ESITO_OK;
			if (!ok) {
				logger.error("bl-attivazione-mandato KO - businessID = {}, http = {}, esito = {}",
						contesto.businessId(), response.getStatusCode().value(),
						body != null ? body.getEsito() : null);
			}
			return ok;
		} catch (RestClientException e) {
			// il messaggio dell'eccezione puo' contenere il body della risposta: si logga solo il tipo
			logger.error("bl-attivazione-mandato non raggiungibile o in errore - businessID = {}, errore = {}",
					contesto.businessId(), e.getClass().getSimpleName());
			return false;
		} catch (RuntimeException e) {
			logger.error("bl-attivazione-mandato errore inatteso - businessID = {}, errore = {}",
					contesto.businessId(), e.getClass().getSimpleName());
			return false;
		}
	}

}
