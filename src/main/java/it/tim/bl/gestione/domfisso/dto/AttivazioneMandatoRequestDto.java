package it.tim.bl.gestione.domfisso.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import it.tim.bl.gestione.domfisso.dto.AttivazioneRequestDto.DatiIsap;
import it.tim.bl.gestione.domfisso.dto.AttivazioneRequestDto.Indirizzo;
import it.tim.bl.gestione.domfisso.dto.AttivazioneRequestDto.PersFisica;
import it.tim.bl.gestione.domfisso.dto.AttivazioneRequestDto.PersGiur;
import it.tim.bl.gestione.domfisso.dto.AttivazioneRequestDto.UtenzaMobile;
import lombok.Data;
import lombok.ToString;

/**
 * Tracciato di richiesta SIF bl-attivazione-mandato v1.1 (senza {@code bic}, assente nel tracciato dom-fisso).
 * L'iban viaggia cifrato; iban e codice autorizzativo sono esclusi dal toString.
 */
@Data
public class AttivazioneMandatoRequestDto {

	private RichiestaAttivazioneMandato richiestaAttivazioneMandato;

	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class RichiestaAttivazioneMandato {
		private String tipoOperazione;
		private String subsys;
		private String dataOraOp;
		private String codiceDealer;
		private String tidOrdine;
		@JsonProperty("isPersFisica")
		private String isPersFisica;
		@JsonProperty("isPersGiur")
		private String isPersGiur;
		private PersFisica persFisica;
		private PersGiur persGiur;
		private Indirizzo indirizzo;
		private DatiContoCorrente datiContoCorrente;
		private UtenzaMobile utenzaMobile;
		@JsonProperty("datiISAP")
		private DatiIsap datiISAP;
	}

	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class DatiContoCorrente {
		@ToString.Exclude
		private String iban;
		@ToString.Exclude
		private String codiceAutorizzativo;
	}

}
