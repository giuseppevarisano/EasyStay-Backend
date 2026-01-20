package it.easystay.dto;

/**
 * DTO per catturare le credenziali durante l'autenticazione.
 */
public record AuthenticationRequestDTO(String email, String password) {}