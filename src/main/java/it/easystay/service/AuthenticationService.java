package it.easystay.service;

import it.easystay.dto.AuthenticationRequestDTO;
import it.easystay.dto.AuthenticationResponseDTO;
import it.easystay.dto.RegisterRequestDTO;
import it.easystay.dto.RegisterResponseDTO;
import it.easystay.exception.CustomDuplicateException;
import it.easystay.mapper.UtenteMapper;
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
    private final UtenteMapper utenteMapper; // <-- Iniettiamo il nuovo mapper

    public RegisterResponseDTO register(RegisterRequestDTO request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new CustomDuplicateException("email", "Questa email è già registrata");
        }

        // 1. Usiamo il mapper per creare l'Entity (la password viene ignorata nel mapper)
        var utente = utenteMapper.toEntity(request);

        // 2. Settiamo manualmente la password criptata (fondamentale per la sicurezza!)
        utente.setPassword(passwordEncoder.encode(request.getPassword()));

        // 3. Salviamo
        repository.save(utente);

        // 4. Generiamo il token e usiamo il mapper per la risposta
        var jwtToken = jwtService.generateToken(utente);

        RegisterResponseDTO response = utenteMapper.toRegisterResponse(utente);
        response.setToken(jwtToken); // Aggiungiamo il token generato

        return response;
    }

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        var utente = repository.findByEmail(request.email()).orElseThrow();
        var jwtToken = jwtService.generateToken(utente);

        return new AuthenticationResponseDTO(jwtToken);
    }
}