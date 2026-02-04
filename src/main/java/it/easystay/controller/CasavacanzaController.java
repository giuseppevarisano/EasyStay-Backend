package it.easystay.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.easystay.dto.CasavacanzaRequestDTO;
import it.easystay.dto.CasavacanzaResponseDTO;
import it.easystay.service.CasavacanzaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(
        value = "/api/case",
        produces = MediaType.APPLICATION_JSON_VALUE  // ← Applicato a tutti i metodi
)
@RequiredArgsConstructor
@Tag(name = "Casevacanza", description = "API per la gestione delle case vacanza (creazione e controllo disponibilità)")
public class CasavacanzaController {

    private final CasavacanzaService casavacanzaService;

    @Operation(
            summary = "Crea una nuova Casa vacanza",
            description = "Creazione nuova casa vacanza possibile solo se sei loggato come utente con ruolo ADMIN."
    )
    @PostMapping(value = "crea",consumes = MediaType.APPLICATION_JSON_VALUE)
    public CasavacanzaResponseDTO crea(@RequestBody @Valid CasavacanzaRequestDTO casa) {
        return casavacanzaService.crea(casa);
    }

    // i paramentri sono obbligatori di default altrimenti si dovrebbe usare @RequestParam(required = false)
    @Operation(
            summary = "Cerca Case vacanza disponibilil per una determinata città",
            description = "Ricerca case vacanze disponibili."
    )
    @GetMapping("/disponibili")
    public ResponseEntity<List<CasavacanzaResponseDTO>> cercaDisponibiliPerCitta(
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate inizio,
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fine,
            @RequestParam(defaultValue = "Roma") String citta) {

        if (fine.isBefore(inizio)) {
            return ResponseEntity.badRequest().build();
        }

        List<CasavacanzaResponseDTO> caseVacanzaDisponibili = casavacanzaService.cercaCaseDisponibili(inizio, fine, citta);
        //Spring MVC serializza in JSON automaticamente (tramite Jackson)

        //TODO diamo anche il numero delle casevacanza Disponibili per possibile paginazione futura
        /*RicercaCaseResponse response = new RicercaCaseResponse(
        risultati,
        risultati.size(),
        risultati.isEmpty() ? "Nessuna casa disponibile per la ricerca" : null
    );public class RicercaCaseResponse {
    private List<CasavacanzaResponseDTO> case;
    private int totale;
    private String messaggio;  // Opzionale

    // Constructor, getter, setter
}*/
        return ResponseEntity.ok(caseVacanzaDisponibili);
        //Client (riceve JSON)
    }
}