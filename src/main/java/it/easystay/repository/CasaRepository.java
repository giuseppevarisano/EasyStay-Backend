package it.easystay.repository;

import it.easystay.model.Casavacanza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CasaRepository extends JpaRepository<Casavacanza, Long> {

    // Risposta al problema N+1: Carichiamo tutto con una sola JOIN
    @Query("SELECT c FROM Casavacanza c LEFT JOIN FETCH c.prenotazioni")
    List<Casavacanza> findAllWithPrenotazioni();

    List<Casavacanza> findByCittaIgnoreCase(String citta);
}