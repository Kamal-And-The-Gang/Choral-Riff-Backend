package fr.afpa.choral_riff.mapper;

import fr.afpa.choral_riff.dto.InstrumentDto;
import fr.afpa.choral_riff.entity.Ensemble;
import fr.afpa.choral_riff.entity.Instrument;
import org.mapstruct.*;
import java.util.List;

/**
 * Mapper Mapstruct pour convertir entre les entités et les DTOs
 * 
 */

@Mapper(componentModel = "spring")
public interface InstrumentMapper {
    @Mapping(target = "ensembleId", source = "ensemble.id")
    InstrumentDto toDto(Instrument instrument);

    @Mapping(target = "ensemble", source = "dto.ensembleId", qualifiedByName = "convertIdToEnsemble")
    Instrument toEntity(InstrumentDto dto);

    List<InstrumentDto> toDtoList(List<Instrument> instruments);

    @Mapping(target = "ensemble", source = "dto.ensembleId", qualifiedByName = "convertIdToEnsemble")
    void updateEntityFromDto(InstrumentDto dto, @MappingTarget Instrument entity);

    @Named("convertIdToEnsemble")
    static Ensemble convertIdToEnsemble(Long id) {
        if (id == null) {
            return null;
        }
        Ensemble ensemble = new Ensemble();
        ensemble.setId(id);
        return ensemble;
    }
}
