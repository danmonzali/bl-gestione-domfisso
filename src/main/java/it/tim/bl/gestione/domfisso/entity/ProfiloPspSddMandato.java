package it.tim.bl.gestione.domfisso.entity;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

/**
 * Entity JPA in sola lettura mappata sulla tabella BDATA.PROFILO_PSP_SDD_MANDATO
 * (scritta da bl-attivazione-mandato). Mapping parziale delle colonne utilizzate.
 */
@Entity
@Immutable
@Table(name = "PROFILO_PSP_SDD_MANDATO")
@Getter
public class ProfiloPspSddMandato {

	@Id
	@Column(name = "IDMANDATO")
	private String idMandato;

	@Column(name = "CF_O_PIVA")
	private String cfOPiva;

	@Column(name = "STATOPROFILO")
	private String statoProfilo;

}
