package it.tim.bl.visualizza.richiesta.domfisso.controller;

import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import it.tim.bl.visualizza.richiesta.domfisso.exception.BVRDErrorResponse;
import it.tim.bl.visualizza.richiesta.domfisso.exception.BVRDFException;

@RestControllerAdvice
public class RestExceptionHandler {

	private static final Logger logger = LogManager.getLogger(RestExceptionHandler.class);

	private static final String logPrefix = "bl-visualizza-richiesta-domfisso - ";

	@ExceptionHandler(BVRDFException.class)
	public final ResponseEntity<BVRDErrorResponse> handleBRPException(BVRDFException e) {
		ResponseEntity<BVRDErrorResponse> response = buildResponse(e.getHttpStatusCode(),
				new BVRDErrorResponse(e.getCode(), e.getMessage(), e.getTimeStamp(), "BANKINGDATA"));

		logger.info(logPrefix + " - response = " + response);

		ThreadContext.put("gupEventReturnCode", e.getHttpStatusCode().toString());

		logger.info(logPrefix + " - END OPERATION");

		ThreadContext.remove("gupExeTime");
		ThreadContext.remove("gupEventReturnCode");

		return response;
	}


	@ExceptionHandler(ResponseStatusException.class)
	public final void handle(ResponseStatusException e) {
		throw e;
	}

	
	@ExceptionHandler(Exception.class)
	public final ResponseEntity<BVRDErrorResponse> handle(Exception e) {
		ResponseEntity<BVRDErrorResponse> response = buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
				new BVRDErrorResponse("500", e.getMessage(), LocalDateTime.now(), "BANKINGDATA"));

		logger.error(logPrefix + "ERROR: ", e);
		logger.info(logPrefix + " - response = " + response);

		ThreadContext.put("gupEventReturnCode", "500");
		logger.info(logPrefix+" - END OPERATION");

		ThreadContext.remove("gupExeTime");
		ThreadContext.remove("gupEventReturnCode");

		return response;
	}
	
	public ResponseEntity<BVRDErrorResponse> buildResponse(HttpStatus status, BVRDErrorResponse errorResponse) {
		return ResponseEntity.status(status).body(errorResponse);
	}

}
