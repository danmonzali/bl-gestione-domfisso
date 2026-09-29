package it.tim.bl.gestione.domfisso.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import it.tim.bl.gestione.domfisso.util.MaskingUtil;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VisualizzazioneRichiestaAttivazioneDomiciliazioneFisso {

	    private String tipoOperazione;
	    private String subsys;
	    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
	    private LocalDateTime dataOraOp;
	    private String cf;
	    private UtenzaFissa utenzaFissa;

		@Data
	    public static class UtenzaFissa {
	        private String prefisso;
	        private String numero;

			@Override
			public String toString() {
				return "UtenzaFissa [prefisso=" + MaskingUtil.mask(prefisso) + ", numero=" + MaskingUtil.mask(numero)
						+ "]";
			}
	    }

		@Override
		public String toString() {
			return "VisualizzazioneRichiestaAttivazioneDomiciliazioneFisso [tipoOperazione=" + tipoOperazione
					+ ", subsys=" + subsys + ", dataOraOp=" + dataOraOp + ", cf=" + MaskingUtil.mask(cf)
					+ ", utenzaFissa=" + utenzaFissa + "]";
		}
	    
	    
	}
