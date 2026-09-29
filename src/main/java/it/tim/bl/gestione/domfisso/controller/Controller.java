package it.tim.bl.gestione.domfisso.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.tim.bl.gestione.domfisso.dto.VisualizzaRequestDto;
import it.tim.bl.gestione.domfisso.dto.VisualizzaResponseDto;
import it.tim.bl.gestione.domfisso.exception.BRExceptionReturn;
import it.tim.bl.gestione.domfisso.exception.ErrorResponse;
import it.tim.bl.gestione.domfisso.service.BVRService;
import it.tim.gup.common.controller.GupController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Date;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/bl")
public class Controller extends GupController {

	private static final DateTimeFormatter FORMAT_INTERACTION_DATE = DateTimeFormatter.ofPattern("uuuu-MM-dd")
			.withResolverStyle(ResolverStyle.STRICT);
	private static final DateTimeFormatter FORMAT_INTERACTION_TIME = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
			.withResolverStyle(ResolverStyle.STRICT);

	@Autowired
	private BVRService service;

	private static final Logger logger = LogManager.getLogger(Controller.class);

	@Operation(summary = "GET BL Visualizza richiesta dom fisso", description = "verifica lo stato attuale di una domiciliazione bancaria su mandato generico SDD della bolletta del telefono fisso di un cliente TIM")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Ok: esito 01 (domiciliazione trovata), 02 (controlli formali falliti), 03 (nessuna domiciliazione)", content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = VisualizzaResponseDto.class)) }),
			@ApiResponse(responseCode = "400", description = "Bad request (code 103 - Errore nei dati di input)", content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error (code 674 - Errore generico su GUP)", content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)) }) })
	@CrossOrigin(origins = "*")
	@GetMapping(value = "/visualizza-richiesta-domfisso", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<VisualizzaResponseDto> blVisualizzaRichiestaDomFisso(@RequestBody(required = true) VisualizzaRequestDto request,
			@RequestHeader(name = "sourceSystem", required = true) String sourceSystem,
			@RequestHeader(name = "channel", required = true) String channel,
			@RequestHeader(name = "interactionDate-Date", required = true) String interactionDateDate,
			@RequestHeader(name = "interactionDate-Time", required = true) String interactionDateTime,
			@RequestHeader(name = "sessionID", required = true) String sessionID,
			@RequestHeader(name = "businessID", required = true) String businessID,
			@RequestHeader(name = "transactionID", required = true) String transactionID,
			@RequestHeader(name = "messageID", required = true) String messageID,
			@RequestHeader(name = "resubmitted", required = false) String resubmitted,
			@RequestHeader(name = "APIGW_requestID", required = false) String APIGWRequestID) throws Exception {

		ThreadContext.put("gupEventType", "blVisualizzaRichiestaDomFisso");
		getLogger().info("blVisualizzaRichiestaDomFisso - BEGIN OPERATION");
        getLogger().info("blVisualizzaRichiestaDomFisso - body = {}", request);
        getLogger().info("blVisualizzaRichiestaDomFisso - headerParam - sourceSystem = {}, channel = {}, interactionDate-Date = {}, interactionDate-Time = {}, sessionID = {}, businessID = {}, transactionID = {}, messageID = {}, APIGW_requestID = {}, resubmitted = {}", sourceSystem, channel, interactionDateDate, interactionDateTime, sessionID, businessID, transactionID, messageID, APIGWRequestID, resubmitted);
		Date initDate = new Date();

		try {
			validaInteractionDate(interactionDateDate, interactionDateTime);

			VisualizzaResponseDto responseBody = service.visualizzaRichiestaDomFisso(request);
			ThreadContext.put("gupEventReturnCode", responseBody.getEsito());

			return ResponseEntity.ok(responseBody);
		} finally {
			String exeTime = String.valueOf((new Date().getTime() - initDate.getTime()));
			ThreadContext.put("gupExeTime", exeTime);
			getLogger().info("blVisualizzaRichiestaDomFisso  - END OPERATION");
        }
	}

	private void validaInteractionDate(String interactionDateDate, String interactionDateTime) {
		try {
			LocalDate.parse(interactionDateDate, FORMAT_INTERACTION_DATE);
			LocalTime.parse(interactionDateTime, FORMAT_INTERACTION_TIME);
		} catch (DateTimeParseException e) {
			getLogger().info("blVisualizzaRichiestaDomFisso - formato header interactionDate-Date/interactionDate-Time non valido: {} {}",
					interactionDateDate, interactionDateTime);
			throw BRExceptionReturn.CODE_103();
		}
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}
}
