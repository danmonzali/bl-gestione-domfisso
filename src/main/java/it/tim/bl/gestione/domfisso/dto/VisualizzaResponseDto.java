package it.tim.bl.gestione.domfisso.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VisualizzaResponseDto {

        private String tipoOperazione;
	    private String subsys;
	    private String dataOraRisposta;
	    private String esito;
	    private DatiRichiesta datiRichiesta;

		@Data
		@JsonInclude(JsonInclude.Include.NON_NULL)
	    public static class DatiRichiesta {
	        private String stato;
	        private String dataRichiesta;
	        private String bid;
	        private UtenzaFissa utenzaFissa;
	    }

	    @Data
	    @JsonInclude(JsonInclude.Include.NON_NULL)
	    public static class UtenzaFissa {
	        private String prefisso;
	        private String numero;
	    }
	}
