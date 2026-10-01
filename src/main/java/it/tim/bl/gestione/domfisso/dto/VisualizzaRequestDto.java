package it.tim.bl.gestione.domfisso.dto;

import lombok.Getter;

@Getter
public class VisualizzaRequestDto {

	private VisualizzazioneRichiestaAttivazioneDomiciliazioneFisso visualizzazioneRichiestaAttivazioneDomiciliazioneFisso;

	@Override
	public String toString() {
		return "VisualizzaRequestDto [visualizzazioneRichiestaAttivazioneDomiciliazioneFisso="
				+ visualizzazioneRichiestaAttivazioneDomiciliazioneFisso + "]";
	}

}
