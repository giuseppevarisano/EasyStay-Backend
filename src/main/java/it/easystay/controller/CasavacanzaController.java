package it.easystay.controller;

import it.easystay.dto.CasavacanzaRequestDTO;
import it.easystay.dto.CasavacanzaResponseDTO;
import it.easystay.service.CasavacanzaService;
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
public class CasavacanzaController {

    private final CasavacanzaService casavacanzaService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public CasavacanzaResponseDTO crea(@RequestBody CasavacanzaRequestDTO casa) {
        return casavacanzaService.crea(casa);
    }

    @GetMapping("/disponibili")
    public ResponseEntity<List<CasavacanzaResponseDTO>> cercaDisponibili(
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate inizio,
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fine,
            @RequestParam String citta) {

        if (fine.isBefore(inizio)) {
            return ResponseEntity.badRequest().build();
        }

        List<CasavacanzaResponseDTO> risultati = casavacanzaService.cercaCaseDisponibili(inizio, fine, citta);
        return ResponseEntity.ok(risultati);
    }
}