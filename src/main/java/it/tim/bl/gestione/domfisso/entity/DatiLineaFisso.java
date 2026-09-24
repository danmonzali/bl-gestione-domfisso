package it.tim.bl.gestione.domfisso.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entity JPA mappata sulla tabella BDATA.DATI_LINEA_FISSO.
 * Sono mappati solo i campi utilizzati dal bounded context corrente:
 * numerose colonne della tabella reale contengono dati anagrafici cifrati
 * (KEYID/IV) non necessari a questo servizio.
 */
@Entity
@Table(name = "DATI_LINEA_FISSO")
public class DatiLineaFisso {

	@Id
	@Column(name = "ID_LINEA")
	private Long idLinea;

	@Column(name = "PREFISSO")
	private String prefisso;

	@Column(name = "NUMERO")
	private String numero;

	public Long getIdLinea() {
		return idLinea;
	}

	public void setIdLinea(Long idLinea) {
		this.idLinea = idLinea;
	}

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
