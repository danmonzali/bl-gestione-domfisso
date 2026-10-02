package it.tim.bl.gestione.domfisso.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * Risposta di bl-attivazione-mandato: esito 1 = OK, 2 = KO.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttivazioneMandatoResponseDto {

	private String tipoOperazione;
	private Integer esito;

}
