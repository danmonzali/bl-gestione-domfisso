package it.tim.bl.gestione.domfisso.bean;

import java.time.LocalDateTime;
public class VisualizzazioneRichiestaAttivazioneDomiciliazioneFisso {

	    private String tipoOperazione;
	    private String subsys;
	    private LocalDateTime dataOraOp;
	    private String cf;
	    private UtenzaFissa utenzaFissa;

	    // Getters and Setters
	    public String getTipoOperazione() {
	        return tipoOperazione;
	    }

	    public void setTipoOperazione(String tipoOperazione) {
	        this.tipoOperazione = tipoOperazione;
	    }

	    public String getSubsys() {
	        return subsys;
	    }

	    public void setSubsys(String subsys) {
	        this.subsys = subsys;
	    }

	    public LocalDateTime getDataOraOp() {
	        return dataOraOp;
	    }

	    public void setDataOraOp(LocalDateTime dataOraOp) {
	        this.dataOraOp = dataOraOp;
	    }

	    public String getCf() {
	        return cf;
	    }

	    public void setCf(String cf) {
	        this.cf = cf;
	    }

	    public UtenzaFissa getUtenzaFissa() {
	        return utenzaFissa;
	    }

	    public void setUtenzaFissa(UtenzaFissa utenzaFissa) {
	        this.utenzaFissa = utenzaFissa;
	    }

	    public static class UtenzaFissa {
	        private String prefisso;
	        private String numero;

	        // Getters and Setters
	        public String getPrefisso() {
	            return prefisso;
	        }

	        public void setPrefisso(String prefisso) {
	            this.prefisso = prefisso;
	        }

	        public String getNumero() {
	            return numero;
	        }

	        public void setNumero(String numero) {
	            this.numero = numero;
	        }
	        
	    }

		@Override
		public String toString() {
			return "RequestBVRD [tipoOperazione=" + tipoOperazione + ", subsys=" + subsys + ", dataOraOp=" + dataOraOp
					+ ", cf=" + cf + ", utenzaFissa=" + utenzaFissa + "]";
		}
	    
	    
	}
