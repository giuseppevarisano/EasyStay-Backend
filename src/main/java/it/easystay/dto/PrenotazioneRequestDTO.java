package it.easystay.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PrenotazioneRequestDTO {
    @NotNull
    private Long casaId;

    @FutureOrPresent(message = "La data di inizio non può essere nel passato")
    private LocalDate dataInizio;

    @NotNull
    private LocalDate dataFine;
}