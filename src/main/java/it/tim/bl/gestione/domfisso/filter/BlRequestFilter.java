package it.tim.bl.gestione.domfisso.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro più esterno della catena:
 * <ul>
 * <li>se la request contiene l'header APIGW_requestID, ne riporta il valore nell'header di risposta
 * apigwRequestId (SIF §3.3), sia per le risposte 200 sia per quelle di errore;</li>
 * <li>a fine richiesta rimuove dal ThreadContext le chiavi valorizzate dal servizio, per evitare che
 * finiscano nei log della richiesta successiva servita dallo stesso thread.</li>
 * </ul>
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BlRequestFilter extends OncePerRequestFilter {

	static final String REQUEST_HEADER = "APIGW_requestID";
	static final String RESPONSE_HEADER = "apigwRequestId";
	private static final String[] THREAD_CONTEXT_KEYS = { "gupEventType", "gupExeTime", "gupEventReturnCode" };

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String apigwRequestId = request.getHeader(REQUEST_HEADER);
		if (StringUtils.hasText(apigwRequestId)) {
			response.setHeader(RESPONSE_HEADER, apigwRequestId);
		}
		try {
			filterChain.doFilter(request, response);
		} finally {
			for (String key : THREAD_CONTEXT_KEYS) {
				ThreadContext.remove(key);
			}
		}
	}

}
