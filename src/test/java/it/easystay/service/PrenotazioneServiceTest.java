package it.easystay.service;

import it.easystay.dto.PrenotazioneRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class PrenotazioneServiceTest {

    @Autowired
    private PrenotazioneService prenotazioneService;

    @Test
    void quandoPrenotazioneValida_alloraSalvaConSuccesso() {
        PrenotazioneRequestDTO dto = new PrenotazioneRequestDTO();
        dto.setCasaId(1L);
        dto.setDataInizio(LocalDate.now().plusDays(10));
        dto.setDataFine(LocalDate.now().plusDays(15));

        assertDoesNotThrow(() -> prenotazioneService.salvaPrenotazione(dto,"utente1@esempio.it"));
    }

    @Test
    void quandoCasaNonEsiste_alloraLanciaEccezione() {
        PrenotazioneRequestDTO dto = new PrenotazioneRequestDTO();
        dto.setCasaId(9999L); // ID inesistente
        dto.setDataInizio(LocalDate.now());
        dto.setDataFine(LocalDate.now().plusDays(1));

        assertThrows(RuntimeException.class, () -> prenotazioneService.salvaPrenotazione(dto,"utente1@esempio.it"));
    }
}