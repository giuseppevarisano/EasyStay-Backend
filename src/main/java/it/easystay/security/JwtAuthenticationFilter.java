package it.easystay.security;

import it.easystay.repository.UtenteRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;                // Servizio per trattare i token JWT
    private final UtenteRepository utenteRepository;    // Repository per accedere alla tabella Utente

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // Recupera l'Authorization Header
        String authHeader = request.getHeader("Authorization");

        // Verifica la presenza e la validità dell'header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = authHeader.substring(7);
            String email = jwtService.extractUsername(jwt);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var utente = utenteRepository.findByEmail(email).orElse(null);

                if (utente != null && jwtService.isTokenValid(jwt, utente)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            utente, null, utente.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (ExpiredJwtException e) {
            // Rimuovi il {} perché il tuo logger attuale non lo supporta come segnaposto
            logger.warn("Token JWT scaduto: " + e.getMessage());

        } catch (MalformedJwtException | SignatureException e) {
            logger.warn("Token JWT non valido o firma compromessa: " + e.getMessage());

        } catch (Exception e) {
            // Per loggare l'eccezione intera, passa il messaggio e l'oggetto 'e' separatamente
            logger.error("Errore imprevisto durante l'autenticazione JWT: " + e.getMessage(), e);
        }

        // Passa al prossimo filtro nella catena
        filterChain.doFilter(request, response);
    }
}