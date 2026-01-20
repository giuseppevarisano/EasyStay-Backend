package it.easystay.service;

import it.easystay.dto.AuthenticationRequestDTO;
import it.easystay.dto.AuthenticationResponseDTO;
import it.easystay.dto.RegisterRequestDTO;
import it.easystay.dto.RegisterResponseDTO;
import it.easystay.exception.CustomDuplicateException;
import it.easystay.model.Utente;
import it.easystay.repository.UtenteRepository;
import it.easystay.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UtenteRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public RegisterResponseDTO register(RegisterRequestDTO request) {

        // Trasformiamo il DTO in Entity (Mapping manuale)
        var utente = Utente.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                // Criptiamo la password presa dal DTO
                .password(passwordEncoder.encode(request.getPassword()))
                .ruolo(request.getRuolo())
                .build();

        if (repository.existsByEmail(request.getEmail())) {
            throw new CustomDuplicateException("email", "Questa email è già registrata");
        }

        // Salviamo l'entità nel database
        repository.save(utente);

        // Generiamo il token
        var jwtToken = jwtService.generateToken(utente);
        return RegisterResponseDTO.builder()
                .token(jwtToken)
                .email(utente.getEmail())
                .nome(utente.getNome())
                .build();
    }

    // LOGIN: Verifica le credenziali e restituisce il token
    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        // Se l'autenticazione fallisce, il metodo sopra lancia un'eccezione e il codice si ferma.
        // Se arriviamo qui, l'utente è autenticato con successo.
        var utente = repository.findByEmail(request.email())
                .orElseThrow();

        var jwtToken = jwtService.generateToken(utente);
        return new AuthenticationResponseDTO(jwtToken);
    }
}