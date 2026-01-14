package it.easystay.controller;

import it.easystay.dto.CasavacanzaRequestDTO;
import it.easystay.model.Casavacanza;
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
    public Casavacanza crea(@RequestBody CasavacanzaRequestDTO casa) {
        return casaService.crea(casa);
    }

    @GetMapping("/cerca")
    public List<Casavacanza> cerca(@RequestParam String citta) {
        return casaService.cercaPerCitta(citta);
    }
}