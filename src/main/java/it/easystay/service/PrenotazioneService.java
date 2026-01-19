package it.easystay.service;

import it.easystay.dto.PrenotazioneRequestDTO;
import it.easystay.dto.PrenotazioneResponseDTO;
import it.easystay.exception.StanzaGiaOccupataException;
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

    @Transactional
    public PrenotazioneResponseDTO salvaPrenotazione(PrenotazioneRequestDTO request, String email) {

        // 1. Controllo logico date
        if (request.getDataFine().isBefore(request.getDataInizio())) {
            throw new IllegalArgumentException("La data di fine precede l'inizio");
        }

        try {
            // 2. Recupero Casa con lock pessimista usando EntityManager
            Casavacanza casa = entityManager.find(Casavacanza.class, request.getCasaId(), LockModeType.PESSIMISTIC_WRITE);
            if (casa == null) {
                throw new EntityNotFoundException("Non abbiamo trovato nessuna casa con l'ID: " + request.getCasaId());
            }

            // 3. Controllo disponibilità (mentre abbiamo il lock sulla casa)
            boolean giaOccupata = prenoRepo.existsByCasaAndDataInizioFine(casa, request.getDataInizio(), request.getDataFine());
            if (giaOccupata) {
                throw new StanzaGiaOccupataException(request.getCasaId());
            }

            // 4. Cerchiamo l'utente sul database
            Utente utente = utenteRepo.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("Utente non trovato con email: " + email));

            // 5. Costruzione oggetto Prenotazione
            var nuovaPrenotazione = Prenotazione.builder()
                    .dataInizio(request.getDataInizio())
                    .dataFine(request.getDataFine())
                    .casa(casa)
                    .utente(utente)
                    .build();

            return mapToResponse(prenoRepo.save(nuovaPrenotazione));

        } catch (PessimisticLockException | PessimisticLockingFailureException | PersistenceException ex) {
            // Lock timeout o altri problemi di concorrenza -> rispondi come conflitto
            throw new StanzaGiaOccupataException(request.getCasaId());
        }
    }

    public Page<PrenotazioneResponseDTO> getPrenotazioniPerUtente(Long utenteId, Pageable pageable) {
        return prenoRepo.findByUtenteId(utenteId, pageable).map(this::mapToResponse);
    }

    public List<PrenotazioneResponseDTO> getPrenotazioniSenzaPaginazione(Long utenteId) {
        return prenoRepo.findByUtenteId(utenteId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    // UNICO METODO DI MAPPING (Evita i campi NULL nel JSON)
    private PrenotazioneResponseDTO mapToResponse(Prenotazione p) {
        PrenotazioneResponseDTO res = new PrenotazioneResponseDTO();
        res.setId(p.getId());
        res.setDataInizio(p.getDataInizio());
        res.setDataFine(p.getDataFine());

        if (p.getCasa() != null) {
            res.setCasaId(p.getCasa().getId());
            res.setNomeCasa(p.getCasa().getNome());
        }
        return res;
    }
}
