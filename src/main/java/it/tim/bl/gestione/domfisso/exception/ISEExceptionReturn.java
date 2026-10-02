package it.tim.bl.gestione.domfisso.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ISEExceptionReturn extends RuntimeException {
	@Serial
    private static final long serialVersionUID = 1L;

	private static final String CODICE_674 = "674";
	private static final String CODICE_674_MSG = "Errore generico su GUP";
	String code;
	String message;

	public ISEExceptionReturn() {
	}

	public ISEExceptionReturn(String code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public static ISEExceptionReturn CODICE_674() {
		return new ISEExceptionReturn(CODICE_674,CODICE_674_MSG);
	}
	
	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return ExceptionJsonUtil.toJson(code, message);
	}
}
