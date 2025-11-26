package fr.afpa.choral_riff.mapper;

import fr.afpa.choral_riff.dto.DocumentDto;
import fr.afpa.choral_riff.dto.InstrumentDto;
import fr.afpa.choral_riff.entity.Document;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;



@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @Mapping(source = "utilisateur.id", target = "utilisateurId")
    @Mapping(source = "morceau.id", target = "morceauId")
    @Mapping(source = "id", target = "id_document")
    @Mapping(target = "instruments", expression = "java(mapInstruments(document))")
    DocumentDto toDto(Document document);

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

    Document toEntity(DocumentDto documentDto);
}
