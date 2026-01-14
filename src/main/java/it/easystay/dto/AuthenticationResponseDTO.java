package it.easystay.dto;

/**
 * DTO per inviare il token JWT generato al client.
 */
public record AuthenticationResponseDTO(String token) {}