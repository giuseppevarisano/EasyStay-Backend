package it.easystay.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.easystay.dto.PrenotazioneRequestDTO;
import it.easystay.dto.PrenotazioneResponseDTO;
import it.easystay.service.PrenotazioneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping(
        value = "/api/prenotazioni",
        produces = MediaType.APPLICATION_JSON_VALUE  // ← Applicato a tutti i metodi
)
@RequiredArgsConstructor
@Tag(name = "Prenotazioni", description = "API per la gestione delle prenotazioni delle case vacanza")
public class PrenotazioneController {

    private final PrenotazioneService service;

    @Operation(
            summary = "Crea una nuova prenotazione",
            description = "Verifica la disponibilità della casa e salva la prenotazione nel database."
    )
    @ApiResponse(responseCode = "201", description = "Prenotazione creata con successo")
    @ApiResponse(responseCode = "400", description = "Dati di input non validi o date incoerenti")
    @ApiResponse(responseCode = "404", description = "Casa vacanza non trovata")
    @PostMapping(value= "crea", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PrenotazioneResponseDTO> crea(
            @Valid @RequestBody PrenotazioneRequestDTO request,
            Principal principal) {

        String email = principal.getName();
        PrenotazioneResponseDTO response = service.salvaPrenotazione(request, email);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Recupera le prenotazioni di un utente",
            description = "Restituisce una pagina di prenotazioni filtrate per l'ID utente. Supporta la paginazione (page, size) e l'ordinamento (sort)."
    )
    @GetMapping("/utente/{utenteId}")
    public Page<PrenotazioneResponseDTO> getByUtente(
            @Parameter(description = "ID univoco dell'utente") @PathVariable Long utenteId,
            @ParameterObject Pageable pageable) {

        return service.getPrenotazioniPerUtente(utenteId, pageable);
    }

    // ENDPOINT SENZA PAGINAZIONE
    @Operation(
            summary = "Recupera TUTTE le prenotazioni (PERICOLOSO)",
            description = "Rischioso: carica TUTTO in memoria. Se l'utente ha 1 milione di prenotazioni, l'app crasha (OutOfMemory)."
    )
    @GetMapping("/utente/{utenteId}/all")
    public List<PrenotazioneResponseDTO> getByUtenteSenzaPaginazione(@PathVariable Long utenteId) {
        return service.getPrenotazioniSenzaPaginazione(utenteId);
    }
}