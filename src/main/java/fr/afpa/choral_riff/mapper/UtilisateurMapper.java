package fr.afpa.choral_riff.mapper;

import fr.afpa.choral_riff.dto.UtilisateurDto;
import fr.afpa.choral_riff.entity.Utilisateur;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UtilisateurMapper {

    // Mapping entité -> DTO
    UtilisateurDto toDto(Utilisateur utilisateur);

    // Mapping DTO -> entité
    @Mapping(target = "morceauxCree", ignore = true)
    @Mapping(target = "documentsAjoutes", ignore = true)
    @Mapping(target = "utilisateurEnsembles", ignore = true) // <--- changé ici
    @Mapping(target = "motDePasse", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    Utilisateur toEntity(UtilisateurDto dto);

    // Pour les listes
    List<UtilisateurDto> toDtoList(List<Utilisateur> utilisateurs);
    List<Utilisateur> toEntityList(List<UtilisateurDto> dtos);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "morceauxCree", ignore = true)
    @Mapping(target = "documentsAjoutes", ignore = true)
    @Mapping(target = "utilisateurEnsembles", ignore = true) // <--- changé ici aussi
    @Mapping(target = "motDePasse", ignore = true)
    void updateEntityFromDto(UtilisateurDto dto, @MappingTarget Utilisateur entity);
}