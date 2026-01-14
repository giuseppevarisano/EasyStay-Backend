package it.easystay.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PrenotazioneResponseDTO {
    private Long id;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private String nomeCasa;
    private Long casaId;
}