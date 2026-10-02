package it.tim.bl.gestione.domfisso.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity JPA mappata sulla tabella BDATA.DATI_LINEA_FISSO (SF-GUP-SDD-modifiche-database v1.2).
 * I dati anagrafici sono persistiti cifrati con le relative colonne _KEYID/_IV.
 */
@Entity
@Table(name = "DATI_LINEA_FISSO")
@Getter
@Setter
public class DatiLineaFisso {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqLineaFissa")
	@SequenceGenerator(name = "seqLineaFissa", sequenceName = "S_ID_LINEA_FISSA", allocationSize = 1)
	@Column(name = "ID_LINEA")
	private Long idLinea;

	@Column(name = "CF_INTESTATARIO")
	private String cfIntestatario;

	@Column(name = "PREFISSO")
	private String prefisso;

	@Column(name = "NUMERO")
	private String numero;

	@Column(name = "DATA_ATTIVAZIONE_LINEA")
	private LocalDate dataAttivazioneLinea;

	@Column(name = "COMUNE_PROV")
	private String comuneProv;

	@Column(name = "COMUNE_PROV_KEYID")
	private String comuneProvKeyid;

	@Column(name = "COMUNE_PROV_IV")
	private String comuneProvIv;

	@Column(name = "INDIRIZZO")
	private String indirizzo;

	@Column(name = "INDIRIZZO_KEYID")
	private String indirizzoKeyid;

	@Column(name = "INDIRIZZO_IV")
	private String indirizzoIv;

	@Column(name = "CAP")
	private String cap;

	@Column(name = "CAP_KEYID")
	private String capKeyid;

	@Column(name = "CAP_IV")
	private String capIv;

	@Column(name = "NOME_INTESTATARIO")
	private String nomeIntestatario;

	@Column(name = "NOME_KEYID")
	private String nomeKeyid;

	@Column(name = "NOME_IV")
	private String nomeIv;

}
