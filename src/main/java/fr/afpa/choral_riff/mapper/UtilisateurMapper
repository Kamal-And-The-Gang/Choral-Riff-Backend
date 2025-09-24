package main.java.fr.afpa.choral_riff.mapper;

import main.java.fr.afpa.choral_riff.dto.UtilisateurDto;
import main.java.fr.afpa.choral_riff.entity.Utilisateur;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UtilisateurMapper {

    // Mapping entité -> DTO
    UtilisateurDto toDto(Utilisateur utilisateur);

    // Mapping DTO -> entité
    @Mapping(target = "morceauxCree", ignore = true)
    @Mapping(target = "documentsAjoutes", ignore = true)
    @Mapping(target = "ensembles", ignore = true)
    @Mapping(target = "motDePasse", ignore = true)
    @Mapping(target = "authorities", ignore = true) // On utilise Spring Security, la classe Utilisateur implémente une interface spécifique à la sécurité : UserDetails.
    Utilisateur toEntity(UtilisateurDto dto);

    // Pour les listes
    List<UtilisateurDto> toDtoList(List<Utilisateur> utilisateurs);

    List<Utilisateur> toEntityList(List<UtilisateurDto> dtos);

    // Mise à jour partielle
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "morceauxCree", ignore = true)
    @Mapping(target = "documentsAjoutes", ignore = true)
    @Mapping(target = "ensembles", ignore = true)
    @Mapping(target = "motDePasse", ignore = true)
    void updateEntityFromDto(UtilisateurDto dto, @MappingTarget Utilisateur entity);
}
