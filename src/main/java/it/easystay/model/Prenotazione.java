package it.easystay.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"casa", "utente"})
public class Prenotazione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dataInizio;
    private LocalDate dataFine;

    @ManyToOne
    @JoinColumn(name = "casa_id")
    private Casavacanza casa;

    @ManyToOne
    @JoinColumn(name = "utente_id")
    private Utente utente;
}