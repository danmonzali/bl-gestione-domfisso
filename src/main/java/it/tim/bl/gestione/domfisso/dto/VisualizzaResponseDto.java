package it.tim.bl.gestione.domfisso.dto;

import lombok.Data;

@Data
public class VisualizzaResponseDto {

        private String tipoOperazione;
	    private String subsys;
	    private String dataOraRisposta;
	    private String esito;
	    private DatiRichiesta datiRichiesta;

		@Data
	    public static class DatiRichiesta {
	        private String stato;
	        private String dataRichiesta;
	        private String bid;
	        private UtenzaFissa utenzaFissa;
	    }

	    @Data
	    public static class UtenzaFissa {
	        private String prefisso;
	        private String numero;
	    }
	}
