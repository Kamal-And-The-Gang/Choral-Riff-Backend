package fr.afpa.choral_riff.mapper;

import fr.afpa.choral_riff.dto.DocumentDto;
import fr.afpa.choral_riff.entity.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    // ENTITY → DTO
    @Mapping(source = "utilisateur.id", target = "utilisateurId")
    @Mapping(source = "morceau.id", target = "morceauId")
    @Mapping(source = "id", target = "id_document")
    // @Mapping(target = "documentInstruments", ignore = true)  // <-- important ici
    DocumentDto toDto(Document document);

    // DTO → ENTITY
    @Mapping(target = "utilisateur", ignore = true)
    @Mapping(target = "morceau", ignore = true)
    @Mapping(source = "id_document", target = "id")
    @Mapping(target = "documentInstruments", ignore = true)  // optionnel ici si tu ne veux pas gérer ça
    Document toEntity(DocumentDto dto);

    // Mise à jour ENTITY depuis DTO
    @Mapping(target = "utilisateur", ignore = true)
    @Mapping(target = "morceau", ignore = true)
    @Mapping(source = "id_document", target = "id")
    @Mapping(target = "documentInstruments", ignore = true)
    void updateEntityFromDto(DocumentDto dto, @MappingTarget Document entity);

}
