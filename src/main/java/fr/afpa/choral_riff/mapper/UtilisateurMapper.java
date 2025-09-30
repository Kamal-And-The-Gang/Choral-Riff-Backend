package fr.afpa.choral_riff.mapper;

import fr.afpa.choral_riff.dto.UtilisateurDto;
import fr.afpa.choral_riff.entity.Utilisateur;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UtilisateurMapper {

    // Mapping entité -> DTO
    UtilisateurDto toDto(Utilisateur utilisateur);

    /**
     * Convertit un DTO UtilisateurDto en entité Utilisateur.
     * Certains champs et associations complexes sont ignorés pour éviter
     * les problèmes de récursivité ou de données incomplètes.
     * motDePasse et authorities sont également ignorés pour éviter d'écraser des
     * données sensibles ou spécifiques à Spring Security.
     */

    // Mapping DTO -> entité
    @Mapping(target = "morceauxCree", ignore = true)
    @Mapping(target = "documentsAjoutes", ignore = true)
    @Mapping(target = "ensembles", ignore = true)
    @Mapping(target = "motDePasse", ignore = true)
    @Mapping(target = "authorities", ignore = true) // On utilise Spring Security, la classe Utilisateur implémente une
                                                    // interface spécifique à la sécurité : UserDetails.
    Utilisateur toEntity(UtilisateurDto dto);

    // Pour les listes
    List<UtilisateurDto> toDtoList(List<Utilisateur> utilisateurs);

    List<Utilisateur> toEntityList(List<UtilisateurDto> dtos);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "morceauxCree", ignore = true)
    @Mapping(target = "documentsAjoutes", ignore = true)
    @Mapping(target = "ensembles", ignore = true)
    @Mapping(target = "motDePasse", ignore = true)
    void updateEntityFromDto(UtilisateurDto dto, @MappingTarget Utilisateur entity);
}
