package main.java.fr.afpa.choral_riff.mapper;



import fr.afpa.choral_riff.dto.EnsembleDto;
import fr.afpa.choral_riff.entity.Ensemble;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring") 
public interface EnsembleMapper {

    // Conversion ENTITY -> DTO
    @Mapping(source = "idEnsemble", target = "id_Ensemble")
    EnsembleDto toDto(Ensemble ensemble);

    // Conversion DTO -> ENTITY
    @Mapping(source = "id_Ensemble", target = "idEnsemble")
    @Mapping(target = "invitations", ignore = true)
    Ensemble toEntity(EnsembleDto dto);

    // Liste
    List<EnsembleDto> toDtoList(List<Ensemble> ensembles);

    // Mise à jour partielle d'une entité
    @Mapping(source = "id_Ensemble", target = "idEnsemble")
    @Mapping(target = "invitations", ignore = true)
    void updateEntityFromDto(EnsembleDto dto, @MappingTarget Ensemble entity);
}
