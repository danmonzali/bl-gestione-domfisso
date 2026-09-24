package it.tim.bl.gestione.domfisso.controller;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.tim.bl.gestione.domfisso.bean.RequestBVRD;
import it.tim.bl.gestione.domfisso.bean.ResponseBVRD;
import it.tim.bl.gestione.domfisso.exception.BVRDErrorResponse;
import it.tim.bl.gestione.domfisso.exception.BVRDFException;
import it.tim.bl.gestione.domfisso.service.BVRService;
import it.tim.gup.common.bean.GupRequestObject;
import it.tim.gup.common.controller.GupController;
import it.tim.gup.common.controller.RestHeader;
import it.tim.gup.common.mapper.GupObjectMapper;

@RestController
@CrossOrigin(origins = "*")
//@RequestMapping(value = "/bl/visualizza/richiesta/domfisso")
public class Controller extends GupController {
	
	@Autowired
	private GupObjectMapper gupObjectMapper;
	
	@Autowired
	private BVRService service;

	private static final Logger logger = LogManager.getLogger(Controller.class);

	@Operation(summary = "POST BL Visualizza richiesta dom fisso", description = "verifica lo stato attuale di una domiciliazione bancaria su mandato generico SDD della bolletta del telefono fisso di un cliente TIM")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok"),
			@ApiResponse(responseCode = "404", description = "Not Found", content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = BVRDErrorResponse.class))) }),
			@ApiResponse(responseCode = "400", description = "Bad request", content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = BVRDErrorResponse.class))) }),
			@ApiResponse(responseCode = "504", description = "GateWay TimeOut", content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = BVRDErrorResponse.class))) }),
			@ApiResponse(responseCode = "503", description = "Service Unavailable", content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = BVRDErrorResponse.class))) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = BVRDErrorResponse.class))) }) })
	@CrossOrigin(origins = "*")
	@PostMapping(value = "/bl/visualizza-richiesta-domfisso")
	//per stampare la request utilizza GupRequestObject
	public ResponseEntity<ResponseBVRD> blVisualizzaRichiestaDomFisso(@RequestBody(required = true) RequestBVRD request,
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

		ResponseEntity<ResponseBVRD> response = null;
		getLogger().info("blVisualizzaRichiestaDomFisso - BEGIN OPERATION");
		getLogger().info("blVisualizzaRichiestaDomFisso - body = " + request);
		getLogger().info("blVisualizzaRichiestaDomFisso - headerParam - sourceSystem = " + sourceSystem + ", channel = " + channel + ", interactionDate-Date = " + interactionDateDate + ", interactionDate-Time = " + interactionDateTime + ", sessionID = " + sessionID + ", businessID = " + businessID + ", transactionID = " + transactionID + ", messageID = " + messageID + ", APIGW_requestID = " + APIGWRequestID + ", resubmitted = " + resubmitted);
		Date initDate = new Date();
		
		try {
			ThreadContext.put("gupEventType", "blVisualizzaRichiestaDomFisso");
			service.validaRequest(request);
			 response = service.visualizzaRichiestaDomFisso(request);
			getLogger().info("blVisualizzaRichiestaDomFisso - response = " + response);
			ThreadContext.put("gupEventReturnCode", "OK");
			String exeTime = String.valueOf((new Date().getTime() - initDate.getTime()));
			ThreadContext.put("gupExeTime", exeTime);
			getLogger().info("blVisualizzaRichiestaDomFisso - END OPERATION");
			ThreadContext.remove("gupExeTime");
			ThreadContext.remove("gupEventReturnCode");
		}catch (BVRDFException e) {
			String exeTime = String.valueOf((new Date().getTime() - initDate.getTime()));
			ThreadContext.put("gupExeTime", exeTime);
			e.setErrorSourceSystem("BANKINGDATA");
			throw e;
		} catch (Exception e) {
			ThreadContext.put("gupEventReturnCode", "KO");
			String exeTime = String.valueOf((new Date().getTime() - initDate.getTime()));
			ThreadContext.put("gupExeTime", exeTime);
			throw e;
		} finally {
		}
		return response;
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

}
