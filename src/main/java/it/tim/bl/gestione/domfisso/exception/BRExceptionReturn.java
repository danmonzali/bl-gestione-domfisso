package it.tim.bl.gestione.domfisso.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BRExceptionReturn extends RuntimeException {

	@Serial
    private static final long serialVersionUID = 1L;

	static final String CODE_103 = "103";
	static final String CODE_103_MSG = "Errore nei dati di input";

	
	String code;
	String message;

	public BRExceptionReturn(String code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public static BRExceptionReturn CODE_103() {
		return new BRExceptionReturn(CODE_103,CODE_103_MSG);
	}

	@Override
	public String toString() {
		return ExceptionJsonUtil.toJson(code, message);
	}
	
	
}
