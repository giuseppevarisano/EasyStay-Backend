package it.easystay.repository;

import it.easystay.model.Casavacanza;
import it.easystay.model.Prenotazione;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Long> {

    // Questa query controlla se ci sono sovrapposizioni:
    // Una prenotazione esiste se (NuovoInizio < FineEsistente) AND (NuovaFine > InizioEsistente)
    @Query("SELECT COUNT(p) > 0 FROM Prenotazione p " +
            "WHERE p.casa = :casa " +
            "AND :dataInizio <= p.dataFine " +
            "AND :dataFine >= p.dataInizio")
    boolean existsByCasaAndDataInizioFine(
            @Param("casa") Casavacanza casa,
            @Param("dataInizio") LocalDate dataInizio,
            @Param("dataFine") LocalDate dataFine
    );

    // Questo metodo filtrerà per utente E paginerà i risultati
    Page<Prenotazione> findByUtenteId(Long utenteId, Pageable pageable);

    // Versione senza paginazione
    List<Prenotazione> findByUtenteId(Long utenteId);
}