package fr.afpa.choral_riff.mapper;

import fr.afpa.choral_riff.dto.InstrumentDto;
import fr.afpa.choral_riff.entity.Instrument;

import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InstrumentMapper {

    InstrumentDto toDto(Instrument instrument);

    @Mapping(target = "documents", ignore = true)
    @Mapping(target = "ensemble", ignore = true)
    Instrument toEntity(InstrumentDto dto);

    List<InstrumentDto> toDtoList(List<Instrument> instruments);

    @Mapping(target = "documents", ignore = true)
    @Mapping(target = "ensemble", ignore = true)
    void updateEntityFromDto(InstrumentDto dto, @MappingTarget Instrument entity);
}
