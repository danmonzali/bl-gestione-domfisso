package it.tim.bl.visualizza.richiesta.domfisso.repo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import it.tim.bl.visualizza.richiesta.domfisso.controller.Controller;
import it.tim.bl.visualizza.richiesta.domfisso.exception.BVRDFException;
import it.tim.bl.visualizzazione.richiesta.domfisso.dto.DomiciliazioneFissoDTO;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class DomiciliazioneFissoRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private static final Logger logger = LogManager.getLogger(DomiciliazioneFissoRepository.class);

	public List<DomiciliazioneFissoDTO> findByPrefissoAndNumero(String prefisso, String numero) throws BVRDFException {
		String sql = "SELECT dsf.STATO AS STATO, "
				+ "to_char(dsf.DATA_INSERIMENTO, 'YYYY-MM-DD HH24:MI:SS')||'.000' AS DATA_INSERIMENTO, "
				+ "dsf.BID AS BID, " + "dlf.PREFISSO AS PREFISSO, " + "dlf.NUMERO AS NUMERO"
				+ " FROM DOMICILIAZIONI_SU_FISSO dsf " + "JOIN DATI_LINEA_FISSO dlf ON (dsf.ID_LINEA = dlf.ID_LINEA) "
				+ " WHERE dlf.PREFISSO = ? " + "AND dlf.NUMERO = ?";
		
		List<Map<String, Object>> rows = null;
		List<DomiciliazioneFissoDTO> results = new ArrayList<>();
		
		try {
			rows = jdbcTemplate.queryForList(sql, new Object[] { prefisso, numero });

			for (Map<String, Object> row : rows) {
				DomiciliazioneFissoDTO dto = new DomiciliazioneFissoDTO();
				dto.setStato(String.valueOf(row.get("STATO")));
				dto.setDataInserimento((String) row.get("DATA_INSERIMENTO"));
				dto.setBid((String) row.get("BID"));
				dto.setPrefisso((String) row.get("PREFISSO"));
				dto.setNumero((String) row.get("NUMERO"));
				results.add(dto);
			}
		} catch (Exception e) {
			throw new BVRDFException(HttpStatus.INTERNAL_SERVER_ERROR, "SDD04",
					"Errore esecuzione operazione sul database di GUP", LocalDateTime.now(), "BANKING", null, null);
		}
		
		logger.info("Result query: " + results.toString());
		
		return results;
	}

	public List<DomiciliazioneFissoDTO> findByCodiceFiscale(String codiceFiscale) {
		String sql = "SELECT dsf.STATO AS STATO, "
				+ "to_char(dsf.DATA_INSERIMENTO, 'YYYY-MM-DD HH24:MI:SS')||'.000' AS DATA_INSERIMENTO, "
				+ "dsf.BID AS BID, " + "dlf.PREFISSO AS PREFISSO, " + "dlf.NUMERO AS NUMERO"
				+ " FROM RICHIESTE_ATT_DOM_FISSO radf "
				+ " JOIN DOMICILIAZIONI_SU_FISSO dsf ON (radf.ID_DOMICILIAZIONE = dsf.ID_DOMICILIAZIONE) "
				+ " JOIN DATI_LINEA_FISSO dlf ON (dsf.ID_LINEA = dlf.ID_LINEA) " + "WHERE radf.CF_INT_MANDATO = ? "
				+ " AND ESITO = '000'";

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, codiceFiscale);
		List<DomiciliazioneFissoDTO> results = new ArrayList<>();

		for (Map<String, Object> row : rows) {
			DomiciliazioneFissoDTO dto = new DomiciliazioneFissoDTO();
			dto.setStato(String.valueOf(row.get("STATO")));
			dto.setDataInserimento((String) row.get("DATA_INSERIMENTO"));
			dto.setBid((String) row.get("BID"));
			dto.setPrefisso((String) row.get("PREFISSO"));
			dto.setNumero((String) row.get("NUMERO"));
			results.add(dto);
		}
		logger.info("Result query: " + results.toString());
		
		return results;
	}

}
