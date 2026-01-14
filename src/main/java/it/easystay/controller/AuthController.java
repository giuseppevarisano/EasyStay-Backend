package it.easystay.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.easystay.dto.AuthenticationResponseDTO;
import it.easystay.dto.LoginRequestDTO;
import it.easystay.dto.RegisterRequestDTO;
import it.easystay.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "1. Autenticazione", description = "Endpoint per la gestione di Accesso e Registrazione")
public class AuthController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(service.authenticate(request));
    }
}