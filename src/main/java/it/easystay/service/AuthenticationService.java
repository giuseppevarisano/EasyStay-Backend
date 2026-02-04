package it.easystay.service;

import it.easystay.dto.AuthenticationRequestDTO;
import it.easystay.dto.AuthenticationResponseDTO;
import it.easystay.dto.RegisterRequestDTO;
import it.easystay.dto.RegisterResponseDTO;
import it.easystay.exception.CustomDuplicateException;
import it.easystay.mapper.UtenteMapper;
import it.easystay.model.Utente;
import it.easystay.repository.UtenteRepository;
import it.easystay.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UtenteMapper utenteMapper; // <-- Iniettiamo il nuovo mapper

    public RegisterResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        if (utenteRepository.existsByEmail(registerRequestDTO.getEmail())) {
            throw new CustomDuplicateException("email", "Questa email è già registrata");
        }

        // 1. Usiamo il mapper per creare l'Entity (la password viene ignorata nel mapper)
        Utente utente = utenteMapper.toEntity(registerRequestDTO);

        // 2. Settiamo manualmente la password criptata (fondamentale per la sicurezza!)
        utente.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));

        // 3. Salviamo (aggiunto @ExceptionHandler(org.springframework.dao.DataAccessException.class)) per gestire problemi
        // di connessione/salvataggio con il DB
        utenteRepository.save(utente);

        // 4. Generiamo il token e usiamo il mapper per la risposta
        var jwtToken = jwtUtils.generateToken(utente);

        RegisterResponseDTO response = utenteMapper.toRegisterResponse(utente);
        response.setToken(jwtToken); // Aggiungiamo il token generato

        return response;
    }

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO authenticationRequestDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequestDTO.email(), authenticationRequestDTO.password())
        );
        // Se fallisce → lancia BadCredentialsException

        Utente utente = utenteRepository.findByEmail(authenticationRequestDTO.email()).orElseThrow();
        var jwtToken = jwtUtils.generateToken(utente);

        return new AuthenticationResponseDTO(jwtToken);
    }
}