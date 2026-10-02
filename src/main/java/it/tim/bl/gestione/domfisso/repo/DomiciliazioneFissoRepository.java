package it.tim.bl.gestione.domfisso.repo;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.tim.bl.gestione.domfisso.entity.DomiciliazioneFisso;

public interface DomiciliazioneFissoRepository extends JpaRepository<DomiciliazioneFisso, Long> {

	@Query("SELECT d FROM DomiciliazioneFisso d "
			+ "JOIN FETCH d.datiLineaFisso l "
			+ "WHERE l.prefisso = :prefisso AND l.numero = :numero "
			+ "ORDER BY d.dataInserimento DESC NULLS LAST")
	List<DomiciliazioneFisso> findByPrefissoAndNumero(@Param("prefisso") String prefisso,
			@Param("numero") String numero);

	boolean existsByBid(String bid);

	@Query("SELECT COUNT(d) FROM DomiciliazioneFisso d "
			+ "JOIN d.datiLineaFisso l "
			+ "WHERE l.prefisso = :prefisso AND l.numero = :numero "
			+ "AND d.stato NOT IN :statiEsclusi")
	long countDomiciliazioniInCorsoSuLinea(@Param("prefisso") String prefisso, @Param("numero") String numero,
			@Param("statiEsclusi") Collection<Integer> statiEsclusi);

	@Modifying
	@Query(value = "UPDATE DOMICILIAZIONI_SU_FISSO SET ID_MANDATO = :idMandato, DATA_AGGIORNAMENTO = SYSDATE + 1 "
			+ "WHERE ID_DOMICILIAZIONE = :idDomiciliazione", nativeQuery = true)
	int aggiornaMandato(@Param("idDomiciliazione") Long idDomiciliazione, @Param("idMandato") String idMandato);

}
