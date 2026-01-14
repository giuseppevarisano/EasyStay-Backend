package it.easystay.service;

import it.easystay.dto.PrenotazioneRequestDTO;
import it.easystay.dto.PrenotazioneResponseDTO;
import it.easystay.exception.StanzaGiaOccupataException;
import it.easystay.model.Casavacanza;
import it.easystay.model.Prenotazione;
import it.easystay.model.Utente;
import it.easystay.repository.CasaRepository;
import it.easystay.repository.PrenotazioneRepository;
import it.easystay.repository.UtenteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PrenotazioneService {

    private final CasaRepository casaRepo;
    private final PrenotazioneRepository prenoRepo;
    private final UtenteRepository utenteRepo;

    @Transactional
    public PrenotazioneResponseDTO salvaPrenotazione(PrenotazioneRequestDTO request, String email) {

        // 1. Controllo logico date
        if (request.getDataFine().isBefore(request.getDataInizio())) {
            throw new IllegalArgumentException("La data di fine precede l'inizio");
        }

        // 2. Recupero Casa
        Casavacanza casa = casaRepo.findById(request.getCasaId())
                .orElseThrow(() -> new EntityNotFoundException("Non abbiamo trovato nessuna casa con l'ID: " + request.getCasaId()));;

        // 3. Controllo disponibilità
        // query di overlap (Inizio <= FineEsistente AND Fine >= InizioEsistente)
        boolean giaOccupata = prenoRepo.existsByCasaAndDataInizioFine(casa, request.getDataInizio(), request.getDataFine());
        if (giaOccupata) {
            throw new StanzaGiaOccupataException(request.getCasaId());
        }

        // 4.  Cerchiamo l'utente sul database
        Utente utente = utenteRepo.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato con email: " + email));

        // 5. Costruzione oggetto Prenotazione
        var NuovaPrenotazione = Prenotazione.builder()
                .dataInizio(request.getDataInizio())
                .dataFine(request.getDataFine())
                .casa(casa)    // Casa recuperata al punto 2
                .utente(utente) // Utente recuperato al punto 4
                .build();

        return mapToResponse(prenoRepo.save(NuovaPrenotazione));
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