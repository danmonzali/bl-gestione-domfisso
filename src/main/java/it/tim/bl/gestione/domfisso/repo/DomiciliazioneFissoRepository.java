package it.tim.bl.gestione.domfisso.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.tim.bl.gestione.domfisso.entity.DomiciliazioneFisso;

public interface DomiciliazioneFissoRepository extends JpaRepository<DomiciliazioneFisso, Long> {

	@Query("SELECT d FROM DomiciliazioneFisso d "
			+ "JOIN FETCH d.datiLineaFisso l "
			+ "WHERE l.prefisso = :prefisso AND l.numero = :numero")
	List<DomiciliazioneFisso> findByPrefissoAndNumero(@Param("prefisso") String prefisso,
			@Param("numero") String numero);

}
