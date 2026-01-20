package it.easystay.service;

import it.easystay.dto.CasavacanzaRequestDTO;
import it.easystay.dto.CasavacanzaResponseDTO;
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

    public CasavacanzaResponseDTO crea(CasavacanzaRequestDTO request) {
        Casavacanza nuovaCasa = Casavacanza.builder()
                .nome(request.getNome())
                .indirizzo(request.getIndirizzo())
                .citta(request.getCitta())
                .prezzoNotte(request.getPrezzoNotte())
                .build();

        Casavacanza salvata = casaRepository.save(nuovaCasa);
        
        return CasavacanzaResponseDTO.builder()
                .id(salvata.getId())
                .nome(salvata.getNome())
                .indirizzo(salvata.getIndirizzo())
                .citta(salvata.getCitta())
                .prezzoNotte(salvata.getPrezzoNotte())
                .build();
    }

    @Cacheable("casePerCitta")
    public List<CasavacanzaResponseDTO> cercaPerCitta(String citta) {
        System.out.println("Sto andando a leggere nel Database per: " + citta);
        return casaRepository.findByCittaIgnoreCase(citta)
                .stream()
                .map(casa -> CasavacanzaResponseDTO.builder()
                        .id(casa.getId())
                        .nome(casa.getNome())
                        .indirizzo(casa.getIndirizzo())
                        .citta(casa.getCitta())
                        .prezzoNotte(casa.getPrezzoNotte())
                        .build())
                .toList();
    }
}