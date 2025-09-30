package fr.afpa.choral_riff.mapper;

import fr.afpa.choral_riff.dto.MorceauDto;
import fr.afpa.choral_riff.entity.Morceau;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { DocumentMapper.class })
public interface MorceauMapper {
    /**
     * Convertit une entité Morceau en MorceauDto.
     * Les IDs des relations Ensemble et Createur sont extraits.
     */

    @Mapping(source = "ensemble.ensembleId", target = "ensembleId")
    @Mapping(source = "createur.id", target = "createurId")
    MorceauDto toDto(Morceau morceau);

    @Mapping(target = "ensemble", ignore = true)
    @Mapping(target = "createur", ignore = true)
    @Mapping(target = "documents", ignore = true)
    Morceau toEntity(MorceauDto dto);
}
