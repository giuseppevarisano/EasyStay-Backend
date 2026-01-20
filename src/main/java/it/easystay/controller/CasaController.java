package it.easystay.controller;

import it.easystay.dto.CasavacanzaRequestDTO;
import it.easystay.dto.CasavacanzaResponseDTO;
import it.easystay.service.CasaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/case")
@RequiredArgsConstructor
public class CasaController {

    private final CasaService casaService;

    @PostMapping
    public CasavacanzaResponseDTO crea(@RequestBody CasavacanzaRequestDTO casa) {
        return casaService.crea(casa);
    }

    @GetMapping("/cerca")
    public List<CasavacanzaResponseDTO> cerca(@RequestParam String citta) {
        return casaService.cercaPerCitta(citta);
    }
}