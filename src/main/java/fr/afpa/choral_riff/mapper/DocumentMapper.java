package main.java.fr.afpa.choral_riff.mapper;

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
    DocumentDto toDto(Document document);

    // DTO → ENTITY (attention : on ignore les relations complexes ici)
    @Mapping(target = "utilisateur", ignore = true)
    @Mapping(target = "morceau", ignore = true)
    @Mapping(source = "id_document", target = "id")
    Document toEntity(DocumentDto dto);

    // Mise à jour d’une entité existante
    @Mapping(target = "utilisateur", ignore = true)
    @Mapping(target = "morceau", ignore = true)
    @Mapping(source = "id_document", target = "id")
    void updateEntityFromDto(DocumentDto dto, @MappingTarget Document entity);
}
