package it.tim.bl.gestione.domfisso.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Entity JPA mappata sulla tabella BDATA.DOMICILIAZIONI_SU_FISSO.
 * La PK ({@code ID_LINEA}) e' condivisa con {@link DatiLineaFisso} (FK1 sullo
 * schema originale), da qui la relazione @OneToOne con @MapsId.
 * La colonna {@code ID_DOMICILIAZIONE} non e' una PK ne' ha un indice univoco
 * sullo schema reale: e' un identificativo logico usato da
 * {@link RichiestaAttDomFisso} per il join applicativo (nessuna FK reale in
 * DB), per questo resta un campo semplice e non una relazione JPA.
 */
@Entity
@Table(name = "DOMICILIAZIONI_SU_FISSO")
public class DomiciliazioneFisso {

	@Id
	@Column(name = "ID_LINEA")
	private Long idLinea;

	@Column(name = "ID_DOMICILIAZIONE")
	private Long idDomiciliazione;

	@Column(name = "STATO")
	private Integer stato;

	@Column(name = "DATA_INSERIMENTO")
	private LocalDateTime dataInserimento;

	@Column(name = "BID")
	private String bid;

	@OneToOne
	@MapsId
	private DatiLineaFisso datiLineaFisso;

	public Long getIdLinea() {
		return idLinea;
	}

	public void setIdLinea(Long idLinea) {
		this.idLinea = idLinea;
	}

	public Long getIdDomiciliazione() {
		return idDomiciliazione;
	}

	public void setIdDomiciliazione(Long idDomiciliazione) {
		this.idDomiciliazione = idDomiciliazione;
	}

	public Integer getStato() {
		return stato;
	}

	public void setStato(Integer stato) {
		this.stato = stato;
	}

	public LocalDateTime getDataInserimento() {
		return dataInserimento;
	}

	public void setDataInserimento(LocalDateTime dataInserimento) {
		this.dataInserimento = dataInserimento;
	}

	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}

	public DatiLineaFisso getDatiLineaFisso() {
		return datiLineaFisso;
	}

	public void setDatiLineaFisso(DatiLineaFisso datiLineaFisso) {
		this.datiLineaFisso = datiLineaFisso;
	}

}
