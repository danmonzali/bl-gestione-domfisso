package it.tim.bl.gestione.domfisso.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity JPA mappata sulla tabella BDATA.DOMICILIAZIONI_SU_FISSO.
 * La PK e' {@code ID_DOMICILIAZIONE}; {@code ID_LINEA} e' una FK nullable verso
 * {@link DatiLineaFisso} (nulla quando la linea non e' ancora attiva).
 * {@code STATO} e {@code ID_MANDATO} vengono modificati dopo l'inserimento tramite
 * la funzione CAMBIA_STATO_DOM e query native: l'entity non va risalvata dopo il cambio stato.
 */
@Entity
@Table(name = "DOMICILIAZIONI_SU_FISSO")
@Getter
@Setter
public class DomiciliazioneFisso {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqDomiciliazioneFisso")
	@SequenceGenerator(name = "seqDomiciliazioneFisso", sequenceName = "S_ID_DOMICILIAZIONE_FISSO", allocationSize = 1)
	@Column(name = "ID_DOMICILIAZIONE")
	private Long idDomiciliazione;

	@Column(name = "STATO")
	private Integer stato;

	@Column(name = "DATA_INSERIMENTO")
	private LocalDateTime dataInserimento;

	@Column(name = "DATA_AGGIORNAMENTO")
	private LocalDateTime dataAggiornamento;

	@Column(name = "COD_STRUMENTO")
	private Long codStrumento;

	@Column(name = "TID")
	private String tid;

	@Column(name = "BID")
	private String bid;

	@Column(name = "ID_MANDATO")
	private String idMandato;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "ID_LINEA")
	private DatiLineaFisso datiLineaFisso;

}
