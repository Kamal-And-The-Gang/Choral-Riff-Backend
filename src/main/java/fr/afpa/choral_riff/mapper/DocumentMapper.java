
package fr.afpa.choral_riff.mapper;

import fr.afpa.choral_riff.dto.DocumentDto;
import fr.afpa.choral_riff.dto.InstrumentDto;
import fr.afpa.choral_riff.entity.Document;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper MapStruct pour convertir entre {@link Document} et
 * {@link DocumentDto}.
 * <p>
 * Gère également la conversion des instruments associés à un document
 * via la méthode {@link #mapInstruments(Document)}.
 * </p>
 * <p>
 * Ce mapper permet de sérialiser les documents pour le front-end tout en
 * évitant les cycles infinis liés aux relations Many-to-Many entre documents et
 * instruments.
 * </p>
 */

@Mapper(componentModel = "spring")
public interface DocumentMapper {
    /**
     * Convertit une entité {@link Document} en DTO {@link DocumentDto}.
     * <p>
     * Les instruments associés sont mappés via {@link #mapInstruments(Document)}.
     * </p>
     *
     * @param document l'entité Document
     * @return le DTO DocumentDto correspondant
     */
    @Mapping(source = "utilisateur.id", target = "utilisateurId")
    @Mapping(source = "morceau.id", target = "morceauId")
    @Mapping(source = "id", target = "id_document")
    @Mapping(source = "urlFichier", target = "urlFichier")
    @Mapping(source = "nomOriginal", target = "nomOriginal") // nouveau champ
    @Mapping(target = "instruments", expression = "java(mapInstruments(document))")
    DocumentDto toDto(Document document);

    default List<InstrumentDto> mapInstruments(Document document) {
        if (document.getInstruments() == null)
            return List.of();

        return document.getInstruments().stream()
                .map(instr -> new InstrumentDto(
                        instr.getId(),
                        instr.getNom(),
                        instr.getDocuments()
                                .stream()
                                .map(d -> d.getId())
                                .collect(Collectors.toSet())))
                .collect(Collectors.toList());
    }

    @Mapping(target = "instruments", ignore = true) // conseillé pour éviter erreurs et cycles
    Document toEntity(DocumentDto documentDto);
}
