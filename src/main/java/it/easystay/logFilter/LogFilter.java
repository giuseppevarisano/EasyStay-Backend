package it.easystay.logFilter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.UUID;

@Component
public class LogFilter implements Filter {

    private static final String TRACE_ID = "traceId";
    private static final String SESSION_ID = "sessionId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // 1. Trace ID: sempre nuovo per ogni richiesta
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put(TRACE_ID, traceId);

        // 2. Session ID: lo prendiamo dalla sessione dell'utente
        // getSession(true) ne crea una se non esiste
        HttpSession session = httpRequest.getSession(true);
        // Recuperiamo l'ID della sessione
        String fullSessionId = session.getId();
        // Verifichiamo la lunghezza: se è maggiore di 6 prendiamo il taglio,
        // altrimenti prendiamo la stringa intera così com'è.
        String sid = (fullSessionId.length() > 6)
                ? fullSessionId.substring(0, 6)
                : fullSessionId;

        MDC.put(SESSION_ID, sid); // Prendiamo solo i primi 6 caratteri
        MDC.put(SESSION_ID, sid);

        try {
            chain.doFilter(request, response);
        } finally {
            // Puliamo tutto per il prossimo thread
            MDC.remove(TRACE_ID);
            MDC.remove(SESSION_ID);
        }
    }
}