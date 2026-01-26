package it.easystay.mapper;

import it.easystay.dto.PrenotazioneRequestDTO;
import it.easystay.dto.PrenotazioneResponseDTO;
import it.easystay.model.Prenotazione;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PrenotazioneMapper {

    @Mapping(source = "casa.id", target = "casaId")
    @Mapping(source = "casa.nome", target = "nomeCasa")
    PrenotazioneResponseDTO toResponseDTO(Prenotazione entity);

    @Mapping(source = "casaId", target = "casa.id")
    Prenotazione toEntity(PrenotazioneRequestDTO dto);

    List<PrenotazioneResponseDTO> toResponseDTOList(List<Prenotazione> entities);
}
