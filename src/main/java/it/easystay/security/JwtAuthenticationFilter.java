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

        // Estrai il token JWT rimuovendo il prefisso "Bearer "
        String jwt = authHeader.substring(7);

        // Estrai l'email (o username) dal token usando il service
        String email = jwtService.extractUsername(jwt);

        // Verifica che l'utente non sia già autenticato
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Recupera l'utente dal database tramite il repository
            var utente = utenteRepository.findByEmail(email)
                    .orElse(null);

            // Se l'utente esiste e il token è valido, autenticalo
            if (utente != null && jwtService.isTokenValid(jwt, utente)) {
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        utente,                       // Oggetto utente autenticato
                        null,                         // Password non necessaria
                        utente.getAuthorities()       // Ruoli e permessi dell'utente
                );

                // Salva l'autenticazione nel contesto di sicurezza
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Passa al prossimo filtro nella catena
        filterChain.doFilter(request, response);
    }
}