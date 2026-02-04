package it.easystay.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "casevacanza")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Casavacanza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false)
    private String indirizzo;

    @Column(nullable = false, name = "prezzo_notte")
    private Double prezzoNotte;

    @Column(nullable = false, length = 50)
    private String citta;

    @Version // OPTIMISTIC LOCK
    private Integer version;
    /*t=0ms:  Mario   → SELECT casa (version=1)
      t=1ms:  Luigi   → SELECT casa (version=1)  ← Stesso version!
      t=50ms: Mario   → UPDATE casa SET version=2 WHERE id=5 AND version=1  ✅
      t=100ms: Luigi  → UPDATE casa SET version=2 WHERE id=5 AND version=1  ❌ FALLISCE!
                  (version è già 2, non 1!)
      t=101ms: Luigi  → OptimisticLockException*/

    // ESCLUDO la lista dal toString per rompere la ricorsione
    /*Quando NON specificate il fetch, JPA usa i default:
        @OneToMany → LAZY (default)
        @ManyToOne → EAGER (default)
        @ManyToMany → LAZY (default)
        @OneToOne → EAGER (default)
        Se voleste specificarlo esplicitamente:

@OneToMany(mappedBy = "casa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)

cascade = CascadeType.ALL
Se salvi una Casavacanza, salva automaticamente tutte le Prenotazioni
Se cancelli una Casavacanza, cancella tutte le prenotazioni associate
Se aggiorni una Casavacanza, aggiorna le prenotazioni*/

    @OneToMany(mappedBy = "casa", cascade = CascadeType.ALL)
    @ToString.Exclude
    /*se usassimo SET invece che LIST
    PRO
        ✅ No duplicati automatici
        ✅ Semanticamente corretto
    CONTRO
        ❌ Devi implementare equals() e hashCode() in Prenotazione
        ❌ Perdi ordine inserimento (a meno di LinkedHashSet)

        List va bene (Hibernate gestisce unicità via DB*/
    private List<Prenotazione> prenotazioni = new ArrayList<>();

}