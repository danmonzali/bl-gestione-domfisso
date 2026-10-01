package it.tim.bl.gestione.domfisso.exception;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Costruisce il body JSON del "Tracciato output in caso negativo per le chiamate REST"
 * comune a tutti i microservizi di gestione mandato: {"code","message","timestamp","errorSourceSystem"}.
 * Centralizza la logica usata dalle classi di eccezione applicative (in precedenza ognuna
 * produceva, tramite toString(), una stringa non conforme allo standard JSON, es. con "=" al
 * posto di ":").
 */
final class ExceptionJsonUtil {

	private static final String ERROR_SOURCE_SYSTEM = "TIM-CO-GUP";
	private static final ZoneId ITALY_ZONE_ID = ZoneId.of("Europe/Rome");

	private static final DateTimeFormatter TIMESTAMP_FORMATTER =
			DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

	private ExceptionJsonUtil() {
	}

	static String toJson(String code, String message) {
		String timestamp = ZonedDateTime.now(ITALY_ZONE_ID).format(TIMESTAMP_FORMATTER);
		return "{\"code\":\"" + escape(code) + "\",\"message\":\"" + escape(message) + "\",\"timestamp\":\""
				+ timestamp + "\",\"errorSourceSystem\":\"" + ERROR_SOURCE_SYSTEM + "\"}";
	}

	private static String escape(String value) {
		return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
	}

}
