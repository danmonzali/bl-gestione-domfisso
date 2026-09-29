package it.tim.bl.gestione.domfisso.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Schema documentale (OpenAPI) del body di errore restituito da {@link GlobalExceptionHandler}
 * e costruito da {@link ExceptionJsonUtil}. Non viene usata a runtime.
 */
@Data
@Schema(name = "ErrorResponse", description = "Tracciato output in caso negativo")
public class ErrorResponse {

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Codice di errore applicativo", allowableValues = { "103", "674" }, example = "103")
	private String code;

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Descrizione dell'errore", example = "Errore nei dati di input")
	private String message;

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Data e ora dell'errore (Europe/Rome, con offset)", example = "2026-09-28T17:10:00.123+02:00")
	private String timestamp;

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Sistema che ha generato l'errore", example = "TIM-CO-GUP")
	private String errorSourceSystem;

}
