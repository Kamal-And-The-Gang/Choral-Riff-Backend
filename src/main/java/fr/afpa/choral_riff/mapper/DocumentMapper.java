package fr.afpa.choral_riff.mapper;

import fr.afpa.choral_riff.dto.DocumentDto;
import fr.afpa.choral_riff.entity.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    /**
     * Convertit une entité Document en DTO DocumentDto.
     * Les IDs des relations sont extraits.
     */

    // ENTITY → DTO
    @Mapping(source = "utilisateur.id", target = "utilisateurId")
    @Mapping(source = "morceau.id", target = "morceauId")
    @Mapping(source = "id", target = "id_document")
    DocumentDto toDto(Document document);

    /**
     * Convertit un DTO DocumentDto en entité Document.
     * Relations complexes ignorées (à gérer manuellement).
     */
    @Mapping(target = "utilisateur", ignore = true)
    @Mapping(target = "morceau", ignore = true)
    @Mapping(source = "id_document", target = "id")
    Document toEntity(DocumentDto dto);

    /**
     * Met à jour une entité Document existante à partir d'un DTO.
     * Relations complexes ignorées.
     */
    @Mapping(target = "utilisateur", ignore = true)
    @Mapping(target = "morceau", ignore = true)
    @Mapping(source = "id_document", target = "id")
    void updateEntityFromDto(DocumentDto dto, @MappingTarget Document entity);

}
