package it.easystay.service;

import it.easystay.dto.PrenotazioneRequestDTO;
import it.easystay.dto.PrenotazioneResponseDTO;
import it.easystay.exception.StanzaGiaOccupataException;
import it.easystay.mapper.PrenotazioneMapper;
import it.easystay.model.Casavacanza;
import it.easystay.model.Prenotazione;
import it.easystay.model.Utente;
import it.easystay.repository.PrenotazioneRepository;
import it.easystay.repository.UtenteRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PessimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrenotazioneService {

    private final EntityManager entityManager;
    private final PrenotazioneRepository prenoRepo;
    private final UtenteRepository utenteRepo;
    private final PrenotazioneMapper prenoMapper; // <-- Aggiunto Mapper

    @Transactional
    public PrenotazioneResponseDTO salvaPrenotazione(PrenotazioneRequestDTO request, String email) {
        if (request.getDataFine().isBefore(request.getDataInizio())) {
            throw new IllegalArgumentException("La data di fine precede l'inizio");
        }

        // 1. PESSIMISTIC LOCK sulla casa
        /*t=0ms:  Mario   → SELECT ... FOR UPDATE (LOCK acquisito ✅)
            t=1ms:  Luigi   → SELECT ... FOR UPDATE (⏳ IN ATTESA...)
            t=50ms: Mario   → Controlla disponibilità (OK)
            t=100ms: Mario  → INSERT prenotazione
            t=150ms: Mario  → COMMIT (LOCK rilasciato ✅)
            t=151ms: Luigi  → LOCK acquisito ✅
            t=200ms: Luigi  → Controlla disponibilità (❌ ora c'è prenotazione Mario!)
            t=250ms: Luigi  → throw StanzaGiaOccupataException*/
        Casavacanza casa = entityManager.find(Casavacanza.class, request.getCasaId(), LockModeType.PESSIMISTIC_WRITE);
        if (casa == null) {
            throw new EntityNotFoundException("Non abbiamo trovato nessuna casa con l'ID: " + request.getCasaId());
        }
        try {

            boolean giaOccupata = prenoRepo.existsByCasaAndDataInizioFine(casa, request.getDataInizio(), request.getDataFine());
            if (giaOccupata) {
                throw new StanzaGiaOccupataException(request.getCasaId());
            }

            Utente utente = utenteRepo.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("Utente non trovato con email: " + email));

            // Possiamo ancora usare il Builder per creare l'Entity perché qui
            // abbiamo già gli oggetti completi (casa e utente) recuperati dal DB.
            var nuovaPrenotazione = Prenotazione.builder()
                    .dataInizio(request.getDataInizio())
                    .dataFine(request.getDataFine())
                    .casa(casa)
                    .utente(utente)
                    .build();

            // Usiamo il mapper per la risposta
            return prenoMapper.toResponseDTO(prenoRepo.save(nuovaPrenotazione));

        } catch (PessimisticLockingFailureException | PersistenceException ex) {
            throw new StanzaGiaOccupataException(request.getCasaId());
        }
    }

    public Page<PrenotazioneResponseDTO> getPrenotazioniPerUtente(Long utenteId, Pageable pageable) {
        // Molto più pulito: il mapper gestisce la conversione di ogni elemento della pagina
        return prenoRepo.findByUtenteId(utenteId, pageable).map(prenoMapper::toResponseDTO);
    }

    public List<PrenotazioneResponseDTO> getPrenotazioniSenzaPaginazione(Long utenteId) {
        return prenoMapper.toResponseDTOList(prenoRepo.findByUtenteId(utenteId));
    }
}
