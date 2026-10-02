package it.tim.bl.gestione.domfisso.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
 * Entity JPA mappata sulla tabella BDATA.RICHIESTE_ATT_DOM_FISSO (SF-GUP-SDD-modifiche-database v1.2).
 * I campi sensibili sono persistiti cifrati con le relative colonne _KEYID/_IV.
 */
@Entity
@Table(name = "RICHIESTE_ATT_DOM_FISSO")
@Getter
@Setter
public class RichiestaAttDomFisso {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqRichiestaAttDomFisso")
	@SequenceGenerator(name = "seqRichiestaAttDomFisso", sequenceName = "S_ID_RICH_ATT_DOM_FISSO", allocationSize = 1)
	@Column(name = "ID_RICHIESTA")
	private Long idRichiesta;

	@Column(name = "ID_DOMICILIAZIONE")
	private Long idDomiciliazione;

	@Column(name = "TID_RICHIESTA_ATT")
	private String tidRichiestaAtt;

	@Column(name = "DATA_RICHIESTA")
	private LocalDateTime dataRichiesta;

	@Column(name = "TIPO_OPERAZIONE")
	private String tipoOperazione;

	@Column(name = "SUBSYS")
	private String subsys;

	@Column(name = "DATA_ORA_OP")
	private String dataOraOp;

	@Column(name = "CODICE_DEALER")
	private String codiceDealer;

	@Column(name = "TID_ORDINE")
	private String tidOrdine;

	@Column(name = "BID")
	private String bid;

	@Column(name = "CF_INT_LINEA")
	private String cfIntLinea;

	@Column(name = "NOME_INT_LINEA")
	private String nomeIntLinea;

	@Column(name = "NOME_INT_LINEA_KEYID")
	private String nomeIntLineaKeyid;

	@Column(name = "NOME_INT_LINEA_IV")
	private String nomeIntLineaIv;

	@Column(name = "PREF_LINEA")
	private String prefLinea;

	@Column(name = "NUM_LINEA")
	private String numLinea;

	@Column(name = "FLAG_LINEA_ATTIVA")
	private String flagLineaAttiva;

	@Column(name = "DATA_ATTIVAZIONE_LINEA")
	private LocalDate dataAttivazioneLinea;

	@Column(name = "COMUNE_INT_LINEA")
	private String comuneIntLinea;

	@Column(name = "COMUNE_INT_LINEA_KEYID")
	private String comuneIntLineaKeyid;

	@Column(name = "COMUNE_INT_LINEA_IV")
	private String comuneIntLineaIv;

	@Column(name = "INDIRIZZO_INT_LINEA")
	private String indirizzoIntLinea;

	@Column(name = "IND_INT_LINEA_KEYID")
	private String indIntLineaKeyid;

	@Column(name = "IND_INT_LINEA_IV")
	private String indIntLineaIv;

	@Column(name = "CAP_INT_LINEA")
	private String capIntLinea;

	@Column(name = "CAP_INT_LINEA_KEYID")
	private String capIntLineaKeyid;

	@Column(name = "CAP_INT_LINEA_IV")
	private String capIntLineaIv;

	@Column(name = "CF_INT_MANDATO")
	private String cfIntMandato;

	@Column(name = "PREF_INT_MANDATO")
	private String prefIntMandato;

	@Column(name = "NUM_INT_MANDATO")
	private String numIntMandato;

	@Column(name = "COMUNE_INT_MAND")
	private String comuneIntMand;

	@Column(name = "COMUNE_INT_MAND_KEYID")
	private String comuneIntMandKeyid;

	@Column(name = "COMUNE_INT_MAND_IV")
	private String comuneIntMandIv;

	@Column(name = "PROVINCIA_INT_MAND")
	private String provinciaIntMand;

	@Column(name = "PROVINCIA_INT_MAND_KEYID")
	private String provinciaIntMandKeyid;

	@Column(name = "PROVINCIA_INT_MAND_IV")
	private String provinciaIntMandIv;

	@Column(name = "PART_TOP_INT_MAND")
	private String partTopIntMand;

	@Column(name = "INDIRIZZO_INT_MAND")
	private String indirizzoIntMand;

	@Column(name = "INDIRIZZO_INT_MAND_KEYID")
	private String indirizzoIntMandKeyid;

	@Column(name = "INDIRIZZO_INT_MAND_IV")
	private String indirizzoIntMandIv;

	@Column(name = "CIVICO_INT_MAND")
	private String civicoIntMand;

	@Column(name = "CAP_INT_MAND")
	private String capIntMand;

	@Column(name = "CAP_INT_MAND_KEYID")
	private String capIntMandKeyid;

	@Column(name = "CAP_INT_MAND_IV")
	private String capIntMandIv;

	@Column(name = "NOME_INT_MAND")
	private String nomeIntMand;

	@Column(name = "NOME_INT_MAND_KEYID")
	private String nomeIntMandKeyid;

	@Column(name = "NOME_INT_MAND_IV")
	private String nomeIntMandIv;

	@Column(name = "COGNOME_INT_MAND")
	private String cognomeIntMand;

	@Column(name = "COGNOME_INT_MAND_KEYID")
	private String cognomeIntMandKeyid;

	@Column(name = "COGNOME_INT_MAND_IV")
	private String cognomeIntMandIv;

	@Column(name = "CODICEAUTORIZZATIVO")
	private String codiceautorizzativo;

	@Column(name = "CODICEAUTORIZZATIVO_KEYID")
	private String codiceautorizzativoKeyid;

	@Column(name = "CODICEAUTORIZZATIVO_IV")
	private String codiceautorizzativoIv;

	@Column(name = "IBAN")
	private String iban;

	@Column(name = "IBAN_KEYID")
	private String ibanKeyid;

	@Column(name = "IBAN_IV")
	private String ibanIv;

	@Column(name = "IDMANDATO")
	private String idmandato;

	@Column(name = "CODOPERAZIONE")
	private String codoperazione;

	@Column(name = "MARCAGGIOCLIENTE")
	private String marcaggiocliente;

	@Column(name = "CODICETERRITORIALE")
	private String codiceterritoriale;

	@Column(name = "PROVENIENZASID")
	private String provenienzasid;

	@Column(name = "PROFILOCONTRATTUALE")
	private String profilocontrattuale;

	@Column(name = "FLAG_PERS_FISICA")
	private String flagPersFisica;

	@Column(name = "CAUSALE_OPERAZIONE")
	private String causaleOperazione;

	@Column(name = "ESITO")
	private String esito;

	@Column(name = "SESSION_ID")
	private String sessionId;

	@Column(name = "BUSINESS_ID")
	private String businessId;

	@Column(name = "TRANSACTION_ID")
	private String transactionId;

	@Column(name = "MESSAGE_ID")
	private String messageId;

	@Column(name = "SOURCESYSTEM")
	private String sourcesystem;

	@Column(name = "CHANNEL")
	private String channel;

}
