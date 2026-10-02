package it.tim.bl.gestione.domfisso.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttivazioneResponseDto {

	private String tipoOperazione;
	private String subsys;
	private String dataOraRisposta;
	private String esito;

}
