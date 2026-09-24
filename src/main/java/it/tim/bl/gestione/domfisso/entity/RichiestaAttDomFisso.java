package it.tim.bl.gestione.domfisso.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity JPA mappata sulla tabella BDATA.RICHIESTE_ATT_DOM_FISSO.
 * Mapping parziale: la tabella reale contiene decine di colonne anagrafiche
 * cifrate (KEYID/IV) non necessarie a questo bounded context.
 */
@Entity
@Table(name = "RICHIESTE_ATT_DOM_FISSO")
public class RichiestaAttDomFisso {

	@Id
	@Column(name = "ID_RICHIESTA")
	private Long idRichiesta;

	@Column(name = "ID_DOMICILIAZIONE")
	private Long idDomiciliazione;

	@Column(name = "CF_INT_MANDATO")
	private String cfIntMandato;

	@Column(name = "ESITO")
	private String esito;

	public Long getIdRichiesta() {
		return idRichiesta;
	}

	public void setIdRichiesta(Long idRichiesta) {
		this.idRichiesta = idRichiesta;
	}

	public Long getIdDomiciliazione() {
		return idDomiciliazione;
	}

	public void setIdDomiciliazione(Long idDomiciliazione) {
		this.idDomiciliazione = idDomiciliazione;
	}

	public String getCfIntMandato() {
		return cfIntMandato;
	}

	public void setCfIntMandato(String cfIntMandato) {
		this.cfIntMandato = cfIntMandato;
	}

	public String getEsito() {
		return esito;
	}

	public void setEsito(String esito) {
		this.esito = esito;
	}

}
