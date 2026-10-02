package it.tim.bl.gestione.domfisso.repo;

import java.sql.CallableStatement;
import java.sql.Types;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Invoca la funzione PL/SQL CAMBIA_STATO_DOM, che storicizza la domiciliazione su
 * STORIA_DOMICILIAZIONE_FISSO e ne aggiorna lo stato. La chiamata partecipa alla transazione
 * JPA corrente: il chiamante deve aver eseguito il flush dell'inserimento.
 */
@Repository
public class CambioStatoDomRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * @return 0 se il cambio stato e' riuscito, 3 o 4 in caso di errore della funzione
	 */
	public int cambiaStato(Long idDomiciliazione, int stato) {
		CallableStatementCreator creator = connection -> {
			CallableStatement cs = connection.prepareCall("{? = call CAMBIA_STATO_DOM(?,?,?,?)}");
			cs.registerOutParameter(1, Types.NUMERIC);
			cs.setString(2, String.valueOf(idDomiciliazione));
			cs.setNull(3, Types.VARCHAR);
			cs.setInt(4, stato);
			cs.setNull(5, Types.VARCHAR);
			return cs;
		};
		Integer esito = jdbcTemplate.execute(creator, (CallableStatementCallback<Integer>) cs -> {
			cs.execute();
			return cs.getInt(1);
		});
		return esito == null ? -1 : esito;
	}

}
