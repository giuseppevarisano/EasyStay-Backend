package it.easystay.mapper;

import it.easystay.dto.CasavacanzaRequestDTO;
import it.easystay.dto.CasavacanzaResponseDTO;
import it.easystay.model.Casavacanza;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CasavacanzaMapper {

    // Da DB a Frontend
    CasavacanzaResponseDTO toResponseDTO(Casavacanza entity);
    List<CasavacanzaResponseDTO> toResponseDTOList(List<Casavacanza> entities);

    // Da Frontend a DB
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "prenotazioni", ignore = true)
    @Mapping(target = "version", ignore = true)
    Casavacanza toEntity(CasavacanzaRequestDTO requestDto);
}