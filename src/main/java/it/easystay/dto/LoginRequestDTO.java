package it.easystay.dto;

/**
 * DTO per catturare le credenziali durante il login.
 */
public record LoginRequestDTO(String email, String password) {}