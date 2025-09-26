package fr.afpa.choral_riff.mapper;

import fr.afpa.choral_riff.dto.InstrumentDto;
import fr.afpa.choral_riff.entity.Instrument;

import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring") // ou "cdi" selon ton projet
public interface InstrumentMapper {

    // Entité → DTO
    InstrumentDto toDto(Instrument instrument);

    // DTO → Entité (documents ignorés ici)
    @Mapping(target = "documents", ignore = true)
    Instrument toEntity(InstrumentDto dto);

    // Liste entités → Liste DTOs
    List<InstrumentDto> toDtoList(List<Instrument> instruments);

    // Mise à jour partielle de l'entité depuis un DTO
    @Mapping(target = "documents", ignore = true)
    void updateEntityFromDto(InstrumentDto dto, @MappingTarget Instrument entity);
}
