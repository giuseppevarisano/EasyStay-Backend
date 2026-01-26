package it.easystay.mapper;

import it.easystay.dto.RegisterRequestDTO;
import it.easystay.dto.RegisterResponseDTO;
import it.easystay.model.Utente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UtenteMapper {

    @Mapping(target = "token", ignore = true) // Il token si genera nel Service, non c'è nell'Entity
    RegisterResponseDTO toRegisterResponse(Utente entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) // La password va criptata nel Service, non mappata direttamente
    Utente toEntity(RegisterRequestDTO dto);
}
