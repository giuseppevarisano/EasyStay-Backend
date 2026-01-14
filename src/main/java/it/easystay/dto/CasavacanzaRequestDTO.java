package it.easystay.dto;

import lombok.Data;

@Data
public class CasavacanzaRequestDTO {
    private String nome;
    private String indirizzo;
    private Double prezzoNotte;
    private String citta;
}