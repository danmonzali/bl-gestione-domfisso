package it.tim.bl.gestione.domfisso.exception;

import java.time.LocalDateTime;
import java.time.ZoneId;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class BVRDErrorResponse {
	private static final long serialVersionUID = -7944769880035493881L;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@JsonProperty(value = "code", required = true)
	private String code;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@JsonProperty(value = "message", required = true)
	private String message;

	@JsonProperty(value = "timestamp", required = true)
	@JsonFormat(shape = JsonFormat.Shape.OBJECT, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
	private LocalDateTime timestamp = LocalDateTime.now(ZoneId.of("UTC"));

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@JsonProperty(value = "errorSourceSystem", required = true)
	private String errorSourceSystem = "Banking Adapter";

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@JsonProperty(value = "moreInfo", required = false)
	private String moreInfo;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@JsonProperty(value = "userMessage", required = false)
	private String userMessage;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getErrorSourceSystem() {
		return errorSourceSystem;
	}

	public void setErrorSourceSystem(String errorSourceSystem) {
		this.errorSourceSystem = errorSourceSystem;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "BVSPErrorResponse [code=" + code + ", message=" + message + ", timestamp=" + timestamp
				+ ", errorSourceSystem=" + errorSourceSystem + ", moreInfo=" + moreInfo + ", userMessage=" + userMessage
				+ "]";
	}

	public BVRDErrorResponse(String code, String message, LocalDateTime timestamp, String errorSourceSystem) {
		super();
		this.code = code;
		this.message = message;
		this.timestamp = timestamp;
		this.errorSourceSystem = errorSourceSystem;
	}

}
