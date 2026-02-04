package it.easystay.repository;

import it.easystay.model.Casavacanza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CasavacanzaRepository extends JpaRepository<Casavacanza, Long> {

    // Risposta al problema N+1: Carichiamo tutto con una sola JOIN
    @Query("SELECT c FROM Casavacanza c LEFT JOIN FETCH c.prenotazioni")
    List<Casavacanza> findAllWithPrenotazioni();

    List<Casavacanza> findByCittaIgnoreCase(String citta);

    @Query("SELECT c FROM Casavacanza c WHERE LOWER(c.citta) = LOWER(:citta) " +
            "AND NOT EXISTS (SELECT p FROM Prenotazione p WHERE p.casa = c AND " +
            "(p.dataInizio <= :dataFine AND p.dataFine >= :dataInizio))")
    List<Casavacanza> cercaCaseDisponibiliPerDateECitta(
            @Param("dataInizio") LocalDate dataInizio,
            @Param("dataFine") LocalDate dataFine,
            @Param("citta") String citta
    );
}