package fr.afpa.choral_riff.mapper;

import fr.afpa.choral_riff.dto.DocumentDto;
import fr.afpa.choral_riff.dto.InstrumentDto;
import fr.afpa.choral_riff.entity.Document;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @Mapping(source = "utilisateur.id", target = "utilisateurId")
    @Mapping(source = "morceau.id", target = "morceauId")
    @Mapping(source = "id", target = "id_document")
    @Mapping(target = "instruments", expression = "java(mapInstruments(document))") // <-- mapping instruments
    DocumentDto toDto(Document document);

    @Mapping(target = "utilisateur", ignore = true)
    @Mapping(target = "morceau", ignore = true)
    @Mapping(source = "id_document", target = "id")
    @Mapping(target = "documentInstruments", ignore = true)
    Document toEntity(DocumentDto dto);

    @Mapping(target = "utilisateur", ignore = true)
    @Mapping(target = "morceau", ignore = true)
    @Mapping(source = "id_document", target = "id")
    @Mapping(target = "documentInstruments", ignore = true)
    void updateEntityFromDto(DocumentDto dto, @MappingTarget Document entity);

    // Méthode utilitaire pour transformer les instruments en DTO
    default List<InstrumentDto> mapInstruments(Document document) {
        if (document.getInstruments() == null) return List.of();
        return document.getInstruments().stream()
                .map(instr -> new InstrumentDto(
                        instr.getId(),
                        instr.getNom(),
                        instr.getEnsembles().stream().map(e -> e.getId()).collect(Collectors.toSet()),
                        instr.getDocuments().stream().map(d -> d.getId()).collect(Collectors.toSet())
                ))
                .toList();
    }
}
