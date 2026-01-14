package it.easystay.service;

import it.easystay.dto.PrenotazioneRequestDTO;
import it.easystay.model.Casavacanza;
import it.easystay.model.Utente;
import it.easystay.repository.PrenotazioneRepository;
import it.easystay.repository.CasaRepository;
import it.easystay.repository.UtenteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PrenotazioneServiceUnitTest {

    @Mock
    private CasaRepository casaRepository;

    @Mock
    private PrenotazioneRepository prenotazioneRepository;

    @Mock
    private UtenteRepository utenteRepository;

    @InjectMocks
    private PrenotazioneService prenotazioneService;

    @Test
    void quandoCasaNonEsiste_DeveLanciareEccezione() {

        Long idInesistente = 99L;
        PrenotazioneRequestDTO request = new PrenotazioneRequestDTO();
        request.setCasaId(idInesistente);
        request.setDataInizio(LocalDate.now());
        request.setDataFine(LocalDate.now().plusDays(1));

        // Simuliamo che il DB restituisca "Vuoto" per questo ID
        when(casaRepository.findById(idInesistente)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            prenotazioneService.salvaPrenotazione(request,"utente1@esempio.it");
        });

        // Verifichiamo che non sia mai stata tentata la riga "save"
        verify(prenotazioneRepository, never()).save(any());
    }

    @Test
    void quandoDatiValidi_alloraSalvaPrenotazioneConSuccesso() {

        Long idCasa = 1L;
        String emailTest = "utente1@esempio.it";

        PrenotazioneRequestDTO request = new PrenotazioneRequestDTO();
        request.setCasaId(idCasa);
        request.setDataInizio(LocalDate.now().plusDays(1));
        request.setDataFine(LocalDate.now().plusDays(5));

        // Creiamo gli oggetti finti (Mock) da restituire
        Casavacanza casaFinta = Casavacanza.builder().id(idCasa).build();
        Utente utenteFinto = Utente.builder().id(1L).email(emailTest).build(); // Creiamo l'utente!

        it.easystay.model.Prenotazione prenotazioneSalvata = new it.easystay.model.Prenotazione();
        prenotazioneSalvata.setId(100L);


        when(casaRepository.findById(idCasa)).thenReturn(Optional.of(casaFinta));

        when(utenteRepository.findByEmail(emailTest)).thenReturn(Optional.of(utenteFinto));

        when(prenotazioneRepository.save(any(it.easystay.model.Prenotazione.class))).thenReturn(prenotazioneSalvata);

        assertDoesNotThrow(() -> prenotazioneService.salvaPrenotazione(request, emailTest));

        verify(prenotazioneRepository, times(1)).save(any());
    }
}