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
	 * ID_DOMICILIAZIONE (PK di DOMICILIAZIONI_SU_FISSO): espresso come theta-join
	 * JPQL perche' non esiste una relazione JPA mappata verso la richiesta.
	 */
	@Query("SELECT d FROM RichiestaAttDomFisso r, DomiciliazioneFisso d "
			+ "LEFT JOIN FETCH d.datiLineaFisso l "
			+ "WHERE r.idDomiciliazione = d.idDomiciliazione "
			+ "AND r.cfIntMandato = :cf AND r.esito = '000' "
			+ "ORDER BY d.dataInserimento DESC NULLS LAST")
	List<DomiciliazioneFisso> findDomiciliazioniByCodiceFiscale(@Param("cf") String cf);

}
