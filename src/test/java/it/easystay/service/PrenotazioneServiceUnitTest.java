package it.easystay.service;

import it.easystay.dto.PrenotazioneRequestDTO;
import it.easystay.model.Casavacanza;
import it.easystay.model.Utente;
import it.easystay.repository.PrenotazioneRepository;
import it.easystay.repository.CasavacanzaRepository;
import it.easystay.repository.UtenteRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
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
    private EntityManager entityManager; // AGGIUNGI QUESTO: è il colpevole dell'errore

    @Mock
    private CasavacanzaRepository casavacanzaRepository;

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


        // Mocka l'entityManager invece del repository
        when(entityManager.find(eq(Casavacanza.class), eq(idInesistente), eq(LockModeType.PESSIMISTIC_WRITE)))
                .thenReturn(null); // Simuliamo che non trovi nulla

        assertThrows(RuntimeException.class, () -> {
            prenotazioneService.salvaPrenotazione(request,"utente1@esempio.it");
        });

        // Verifichiamo che non sia mai stata tentata la riga "save"
        verify(prenotazioneRepository, never()).save(any());
    }

    @Test
    void quandoDatiValidi_alloraSalvaPrenotazioneConSuccesso() {
        // 1. Setup dati
        Long idCasa = 1L;
        String emailTest = "utente1@esempio.it";
        PrenotazioneRequestDTO request = new PrenotazioneRequestDTO();
        request.setCasaId(idCasa);
        request.setDataInizio(LocalDate.now().plusDays(1));
        request.setDataFine(LocalDate.now().plusDays(5));

        Casavacanza casaFinta = Casavacanza.builder().id(idCasa).build();
        Utente utenteFinto = Utente.builder().id(1L).email(emailTest).build();
        it.easystay.model.Prenotazione prenotazioneSalvata = new it.easystay.model.Prenotazione();
        prenotazioneSalvata.setId(100L);

        // 2. MOCK ENTITY MANAGER (Fondamentale perché il service usa find)
        when(entityManager.find(eq(Casavacanza.class), eq(idCasa), eq(LockModeType.PESSIMISTIC_WRITE)))
                .thenReturn(casaFinta);

        // 3. MOCK DISPONIBILITÀ (Diciamo esplicitamente che la stanza è LIBERA)
        when(prenotazioneRepository.existsByCasaAndDataInizioFine(any(), any(), any()))
                .thenReturn(false);

        // 4. MOCK UTENTE
        when(utenteRepository.findByEmail(emailTest)).thenReturn(Optional.of(utenteFinto));

        // 5. MOCK SAVE
        when(prenotazioneRepository.save(any(it.easystay.model.Prenotazione.class)))
                .thenReturn(prenotazioneSalvata);

        // 6. ESECUZIONE E VERIFICA
        assertDoesNotThrow(() -> prenotazioneService.salvaPrenotazione(request, emailTest));
        verify(prenotazioneRepository, times(1)).save(any());
    }
}