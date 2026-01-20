package it.easystay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per restituire i dati di una casa vacanza al client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CasavacanzaResponseDTO {
    private Long id;
    private String nome;
    private String indirizzo;
    private Double prezzoNotte;
    private String citta;
}
