package it.tim.bl.gestione.domfisso.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import it.tim.gup.common.exception.GupException;

public class BVRDFException extends GupException {
	private static final long serialVersionUID = 1L;

	private HttpStatus httpStatusCode;

	private String code;

	private String message;

	private LocalDateTime timeStamp;

	private String moreInfo;

	private String userMessage;

	private String errorSourceSystem;

	public BVRDFException(HttpStatus httpStatusCode) {
		super();
		this.httpStatusCode = httpStatusCode;
	}

	public BVRDFException(HttpStatus httpStatusCode, String code, String message, LocalDateTime timeStamp) {
		super();
		this.httpStatusCode = httpStatusCode;
		this.code = code;
		this.message = message;
		this.timeStamp = timeStamp;
	}

	public BVRDFException(HttpStatus httpStatusCode, String code, String message, LocalDateTime timeStamp,
			String errorSourceSystem, String moreInfo, String userMessage) {
		super();
		this.httpStatusCode = httpStatusCode;
		this.code = code;
		this.message = message;
		this.timeStamp = timeStamp;
		this.errorSourceSystem = errorSourceSystem;
		this.moreInfo = moreInfo;
		this.userMessage = userMessage;
	}

	public BVRDFException(HttpStatus httpStatusCode, String code, String message, LocalDateTime timeStamp,
			String errorSourceSystem) {
		super();
		this.httpStatusCode = httpStatusCode;
		this.code = code;
		this.message = message;
		this.timeStamp = timeStamp;
		this.errorSourceSystem = errorSourceSystem;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public BVRDFException(String message) {
		super(message);
	}

	public HttpStatus getHttpStatusCode() {
		return this.httpStatusCode;
	}

	public void setHttpStatusCode(HttpStatus httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(LocalDateTime timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getMoreInfo() {
		return moreInfo;
	}

	public void setMoreInfo(String moreInfo) {
		this.moreInfo = moreInfo;
	}

	public String getUserMessage() {
		return userMessage;
	}

	public void setUserMessage(String userMessage) {
		this.userMessage = userMessage;
	}

	public String getErrorSourceSystem() {
		return errorSourceSystem;
	}

	public void setErrorSourceSystem(String errorSourceSystem) {
		this.errorSourceSystem = errorSourceSystem;
	}

}
