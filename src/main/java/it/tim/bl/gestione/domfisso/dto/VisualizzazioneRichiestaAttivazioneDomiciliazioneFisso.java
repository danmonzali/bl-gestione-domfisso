package it.tim.bl.gestione.domfisso.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VisualizzazioneRichiestaAttivazioneDomiciliazioneFisso {

	    private String tipoOperazione;
	    private String subsys;
	    private LocalDateTime dataOraOp;
	    private String cf;
	    private UtenzaFissa utenzaFissa;

		@Data
	    public static class UtenzaFissa {
	        private String prefisso;
	        private String numero;
	    }

		@Override
		public String toString() {
			return "VisualizzaRequestDto [tipoOperazione=" + tipoOperazione + ", subsys=" + subsys + ", dataOraOp=" + dataOraOp
					+ ", cf=" + cf + ", utenzaFissa=" + utenzaFissa + "]";
		}
	    
	    
	}
