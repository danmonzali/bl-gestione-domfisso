package it.tim.bl.gestione.domfisso.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

/**
 * Tracciato di richiesta SIF bl-attivazione-dom-fisso v1.0.
 * Iban e codice autorizzativo sono esclusi dal toString per non finire nei log.
 */
@Data
public class AttivazioneRequestDto {

	private RichiestaAttivazioneDomiciliazioneFisso richiestaAttivazioneDomiciliazioneFisso;

	@Data
	public static class RichiestaAttivazioneDomiciliazioneFisso {
		private String tipoOperazione;
		private String subsys;
		private String dataOraOp;
		private String codiceDealer;
		private String tidOrdine;
		private String bid;
		private DatiIntestatario datiIntestatarioLinea;
		private DatiIntestatario datiIntestatarioMandato;
		private DatiContoCorrente datiContoCorrenteAttivazioneDomiciliazione;
		private UtenzaFissa utenzaFissa;
		@JsonProperty("datiISAP")
		private DatiIsap datiISAP;
	}

	@Data
	public static class DatiIntestatario {
		private PersFisica persFisica;
		private PersGiur persGiur;
		private Indirizzo indirizzo;
		private UtenzaMobile utenzaMobile;
	}

	@Data
	public static class PersFisica {
		private String cf;
		private String nome;
		private String cognome;
	}

	@Data
	public static class PersGiur {
		private String piva;
		private String ragSociale;
	}

	@Data
	public static class Indirizzo {
		private String descComune;
		private String siglaProvincia;
		private String partToponomastica;
		private String descIndirizzo;
		private String numCivico;
		private String cap;
	}

	@Data
	public static class UtenzaMobile {
		private String prefisso;
		private String numero;
	}

	@Data
	public static class DatiContoCorrente {
		@ToString.Exclude
		private String iban;
		@ToString.Exclude
		private String codiceAutorizzativo;
		private String idMandato;
	}

	@Data
	public static class UtenzaFissa {
		private String flagLineaAttiva;
		private String prefisso;
		private String numero;
		private String dataAttivazioneLinea;
	}

	@Data
	public static class DatiIsap {
		private String marcaggioCliente;
		private String codiceTerritoriale;
		private String provenienzaSID;
		private String causaleOperazione;
		private String profiloContrattuale;
	}

}
