package it.easystay.service;

import it.easystay.dto.CasavacanzaRequestDTO;
import it.easystay.model.Casavacanza;
import it.easystay.repository.CasaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CasaService {

    private final CasaRepository casaRepository;

    public Casavacanza crea(CasavacanzaRequestDTO request) {
        Casavacanza nuovaCasa = Casavacanza.builder()
                .nome(request.getNome())
                .indirizzo(request.getIndirizzo())
                .citta(request.getCitta())
                .prezzoNotte(request.getPrezzoNotte())
                .build();

        return casaRepository.save(nuovaCasa);
    }

    @Cacheable("casePerCitta")
    public List<Casavacanza> cercaPerCitta(String citta) {
        System.out.println("Sto andando a leggere nel Database per: " + citta);
        return casaRepository.findByCittaIgnoreCase(citta);
    }
}