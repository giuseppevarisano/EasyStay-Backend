package it.easystay.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class CasavacanzaRequestDTO {
    @NotBlank(message = "Il nome è obbligatorio")
    @Size(min = 3, max = 100, message = "Il nome deve essere tra 3 e 100 caratteri")
    private String nome;

    @NotBlank(message = "L'indirizzo è obbligatorio")
    private String indirizzo;

    @NotBlank(message = "Il prezzo per notte è obbligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "Il prezzo deve essere maggiore di 0")
    @DecimalMax(value = "10000.0", message = "Il prezzo non può superare 10000")
    private Double prezzoNotte;

    @NotBlank(message = "La città è obbligatoria")
    private String citta;
}