package it.tim.bl.gestione.domfisso.bean;

import java.time.LocalDateTime;

public class ResponseBVRD {

	    private String tipoOperazione;
	    private String subsys;
	    private String dataOraOp;
	    private String esito;
	    private DatiRichiesta datiRichiesta;

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



	    public String getDataOraOp() {
			return dataOraOp;
		}

		public void setDataOraOp(String dataOraOp) {
			this.dataOraOp = dataOraOp;
		}

		public String getEsito() {
	        return esito;
	    }

	    public void setEsito(String esito) {
	        this.esito = esito;
	    }

	    public DatiRichiesta getDatiRichiesta() {
	        return datiRichiesta;
	    }

	    public void setDatiRichiesta(DatiRichiesta datiRichiesta) {
	        this.datiRichiesta = datiRichiesta;
	    }

	    public static class DatiRichiesta {
	        private String stato;
	        private String dataRichiesta;
	        private String bID;
	        private UtenzaFissa utenzaFissa;

	        // Getters and Setters
	        public String getStato() {
	            return stato;
	        }

	        public void setStato(String stato) {
	            this.stato = stato;
	        }


	        public String getDataRichiesta() {
				return dataRichiesta;
			}

			public void setDataRichiesta(String dataRichiesta) {
				this.dataRichiesta = dataRichiesta;
			}

			public String getbID() {
	            return bID;
	        }

	        public void setbID(String bID) {
	            this.bID = bID;
	        }

	        public UtenzaFissa getUtenzaFissa() {
	            return utenzaFissa;
	        }

	        public void setUtenzaFissa(UtenzaFissa utenzaFissa) {
	            this.utenzaFissa = utenzaFissa;
	        }
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
	}
