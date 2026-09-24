package it.tim.bl.gestione.domfisso.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.tim.bl.gestione.domfisso.entity.DomiciliazioneFisso;
import it.tim.bl.gestione.domfisso.entity.RichiestaAttDomFisso;

public interface RichiestaAttDomFissoRepository extends JpaRepository<RichiestaAttDomFisso, Long> {

	/**
	 * Join applicativo tra RICHIESTE_ATT_DOM_FISSO e DOMICILIAZIONI_SU_FISSO su
	 * ID_DOMICILIAZIONE: non e' una relazione JPA mappata perche' sullo schema
	 * reale quella colonna non e' PK ne' ha un indice univoco su
	 * DOMICILIAZIONI_SU_FISSO (si veda {@link DomiciliazioneFisso}), quindi si
	 * esprime come theta-join JPQL, fedele alla query SQL originale.
	 */
	@Query("SELECT d FROM RichiestaAttDomFisso r, DomiciliazioneFisso d "
			+ "JOIN FETCH d.datiLineaFisso l "
			+ "WHERE r.idDomiciliazione = d.idDomiciliazione "
			+ "AND r.cfIntMandato = :cf AND r.esito = '000'")
	List<DomiciliazioneFisso> findDomiciliazioniByCodiceFiscale(@Param("cf") String cf);

}
