package fr.afpa.choral_riff.mapper;

import fr.afpa.choral_riff.dto.EnsembleDto;
import fr.afpa.choral_riff.entity.Ensemble;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EnsembleMapper {

    // ENTITY -> DTO
    @Mapping(source = "id", target = "id")
    EnsembleDto toDto(Ensemble ensemble);

    // DTO -> ENTITY
    @Mapping(source = "id", target = "id")
    @Mapping(target = "invitations", ignore = true)
    Ensemble toEntity(EnsembleDto dto);

    List<EnsembleDto> toDtoList(List<Ensemble> ensembles);

    // Update partiel
    // @Mapping(source = "id", target = "id")
    @Mapping(target = "invitations", ignore = true)
    void updateEntityFromDto(EnsembleDto dto, @MappingTarget Ensemble entity);
}
