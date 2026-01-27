package it.easystay.service;

import it.easystay.dto.CasavacanzaRequestDTO;
import it.easystay.dto.CasavacanzaResponseDTO;
import it.easystay.mapper.CasavacanzaMapper; // Importa il mapper
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
    private final CasavacanzaMapper casavacanzaMapper; // Iniezione del Mapper

    public CasavacanzaResponseDTO crea(CasavacanzaRequestDTO request) {
        // Da DTO a Entity usando il Mapper
        Casavacanza nuovaCasa = casavacanzaMapper.toEntity(request);

        Casavacanza salvata = casavacanzaRepository.save(nuovaCasa);

        // Da Entity a DTO usando il Mapper
        return casavacanzaMapper.toResponseDTO(salvata);
    }

    @Cacheable("casePerCitta")
    public List<CasavacanzaResponseDTO> cercaPerCitta(String citta) {
        List<Casavacanza> caseTrovate = casavacanzaRepository.findByCittaIgnoreCase(citta);

        // Mappatura della lista automatica
        return casavacanzaMapper.toResponseDTOList(caseTrovate);
    }

    public List<CasavacanzaResponseDTO> cercaCaseDisponibili(LocalDate inizio, LocalDate fine, String citta) {
        // 1. Chiamata al repository
        List<Casavacanza> entitaFound = casavacanzaRepository.findAvailableHouses(inizio, fine, citta);

        // 2. Mappatura automatica (niente più stream e builder manuali!)
        return casavacanzaMapper.toResponseDTOList(entitaFound);
    }
}