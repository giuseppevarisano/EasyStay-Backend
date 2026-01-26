package it.easystay.service;

import it.easystay.dto.CasavacanzaRequestDTO;
import it.easystay.dto.CasavacanzaResponseDTO;
import it.easystay.model.Casavacanza;
import it.easystay.repository.CasavacanzaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CasavacanzaService {

    private final CasavacanzaRepository casavacanzaRepository;

    public CasavacanzaResponseDTO crea(CasavacanzaRequestDTO request) {
        Casavacanza nuovaCasa = Casavacanza.builder()
                .nome(request.getNome())
                .indirizzo(request.getIndirizzo())
                .citta(request.getCitta())
                .prezzoNotte(request.getPrezzoNotte())
                .build();

        Casavacanza salvata = casavacanzaRepository.save(nuovaCasa);
        
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
        return casavacanzaRepository.findByCittaIgnoreCase(citta)
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

    public List<CasavacanzaResponseDTO> cercaCaseDisponibili(LocalDate inizio, LocalDate fine, String citta) {
        // 1. Chiamata al repository (restituisce List<Casavacanza> entità)
        List<Casavacanza> entitaFound = casavacanzaRepository.findAvailableHouses(inizio, fine, citta);

        // 2. Mappatura manuale (o con ModelMapper/MapStruct) da Entity a DTO
        return entitaFound.stream()
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