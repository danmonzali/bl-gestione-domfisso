package it.tim.bl.gestione.domfisso.exception;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class ErrorResponse implements Serializable {
	@Serial
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

//	@Override
//	public String toString() {
//		return "BVSPErrorResponse [code=" + code + ", message=" + message + ", timestamp=" + timestamp
//				+ ", errorSourceSystem=" + errorSourceSystem + ", moreInfo=" + moreInfo + ", userMessage=" + userMessage
//				+ "]";
//	}

}
