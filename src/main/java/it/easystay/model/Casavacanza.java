package it.easystay.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Casavacanza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String indirizzo;
    private Double prezzoNotte;
    private String citta;

    @Version
    private Integer version;

    // ESCLUDO la lista dal toString per rompere la ricorsione
    @OneToMany(mappedBy = "casa", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Prenotazione> prenotazioni = new ArrayList<>();

}