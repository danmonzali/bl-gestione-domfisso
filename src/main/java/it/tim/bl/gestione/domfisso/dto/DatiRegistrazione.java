package it.tim.bl.gestione.domfisso.dto;

import it.tim.bl.gestione.domfisso.dto.AttivazioneMandatoRequestDto;
import it.tim.bl.gestione.domfisso.dto.AttivazioneRequestDto.RichiestaAttivazioneDomiciliazioneFisso;
import it.tim.bl.gestione.domfisso.dto.ContestoRichiesta;
import it.tim.bl.gestione.domfisso.entity.DatiLineaFisso;

/**
 * Dati gia' pronti (normalizzati e cifrati) per la transazione di business.
 *
 * @param datiLinea        riga di DATI_LINEA_FISSO, valorizzata solo con flagLineaAttiva = Y
 * @param mandatoRequest   richiesta per bl-attivazione-mandato, valorizzata solo per tipoOperazione 01/02
 */
public record DatiRegistrazione(ContestoRichiesta contesto, RichiestaAttivazioneDomiciliazioneFisso richiesta,
		DatiLineaFisso datiLinea, Long idRichiesta, String flagPersFisica,
		AttivazioneMandatoRequestDto mandatoRequest) {
}
