package it.tim.bl.gestione.domfisso.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.tim.bl.gestione.domfisso.dto.VisualizzaRequestDto;
import it.tim.bl.gestione.domfisso.dto.VisualizzaResponseDto;
import it.tim.bl.gestione.domfisso.exception.ErrorResponse;
import it.tim.bl.gestione.domfisso.service.BVRService;
import it.tim.gup.common.controller.GupController;
import it.tim.gup.common.mapper.GupObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/bl")
public class Controller extends GupController {
	
	@Autowired
	private GupObjectMapper gupObjectMapper;
	
	@Autowired
	private BVRService service;

	private static final Logger logger = LogManager.getLogger(Controller.class);

	@Operation(summary = "POST BL Visualizza richiesta dom fisso", description = "verifica lo stato attuale di una domiciliazione bancaria su mandato generico SDD della bolletta del telefono fisso di un cliente TIM")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok"),
			@ApiResponse(responseCode = "404", description = "Not Found", content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))) }),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))) }),
			@ApiResponse(responseCode = "504", description = "GateWay TimeOut", content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))) }),
			@ApiResponse(responseCode = "503", description = "Service Unavailable", content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))) }) })
	@CrossOrigin(origins = "*")
	@PostMapping(value = "/visualizza-richiesta-domfisso")
	public ResponseEntity<VisualizzaResponseDto> blVisualizzaRichiestaDomFisso(@RequestBody(required = true) VisualizzaRequestDto request,
			@RequestHeader(name = "sourceSystem", required = true) String sourceSystem,
			@RequestHeader(name = "channel", required = true) String channel,
			@RequestHeader(name = "interactionDate-Date", required = true) String interactionDateDate,
			@RequestHeader(name = "interactionDate-Time", required = true) String interactionDateTime,
			@RequestHeader(name = "sessionID", required = true) String sessionID,
			@RequestHeader(name = "businessID", required = true) String businessID,
			@RequestHeader(name = "transactionID", required = true) String transactionID,
			@RequestHeader(name = "messageID", required = false) String messageID,
			@RequestHeader(name = "resubmitted", required = false) String resubmitted,
			@RequestHeader(name = "APIGW_requestID", required = false) String APIGWRequestID) throws Exception {

		ResponseEntity<VisualizzaResponseDto> response = null;

		ThreadContext.put("gupEventType", "blVisualizzaRichiestaDomFisso");
		getLogger().info("blVisualizzaRichiestaDomFisso - BEGIN OPERATION");
        getLogger().info("blVisualizzaRichiestaDomFisso - body = {}", request);
        getLogger().info("blVisualizzaRichiestaDomFisso - headerParam - sourceSystem = {}, channel = {}, interactionDate-Date = {}, interactionDate-Time = {}, sessionID = {}, businessID = {}, transactionID = {}, messageID = {}, APIGW_requestID = {}, resubmitted = {}", sourceSystem, channel, interactionDateDate, interactionDateTime, sessionID, businessID, transactionID, messageID, APIGWRequestID, resubmitted);
		Date initDate = new Date();

		try {
			response = service.visualizzaRichiestaDomFisso(request);

			return response;
		} finally {
			String exeTime = String.valueOf((new Date().getTime() - initDate.getTime()));
			ThreadContext.put("gupExeTime", exeTime);
			getLogger().info("blVisualizzaRichiestaDomFisso  - END OPERATION");
        }
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}
}
