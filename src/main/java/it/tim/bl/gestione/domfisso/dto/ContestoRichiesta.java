package it.tim.bl.gestione.domfisso.dto;

/**
 * Header SDP della richiesta e TID generato all'inizio del processo.
 */
public record ContestoRichiesta(String sourceSystem, String channel, String interactionDateDate,
		String interactionDateTime, String sessionId, String businessId, String transactionId, String messageId,
		String tid) {

	public ContestoRichiesta conTid(String nuovoTid) {
		return new ContestoRichiesta(sourceSystem, channel, interactionDateDate, interactionDateTime, sessionId,
				businessId, transactionId, messageId, nuovoTid);
	}

}
