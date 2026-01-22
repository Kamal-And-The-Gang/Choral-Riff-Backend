
package fr.afpa.choral_riff.mapper;

import fr.afpa.choral_riff.dto.EnsembleDto;
import fr.afpa.choral_riff.entity.Ensemble;
import fr.afpa.choral_riff.entity.Role;
import fr.afpa.choral_riff.entity.UtilisateurEnsemble;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Set;

/**
 * Mapper MapStruct pour convertir entre {@link Ensemble} et
 * {@link EnsembleDto}.
 * Gère également l'extraction des informations du créateur de l'ensemble.
 */
@Mapper(componentModel = "spring")
public interface EnsembleMapper {

    // -------------------------
    // ENTITY -> DTO
    // -------------------------

    /**
     * Méthode simple pour MapStruct (utilisée pour générer toDtoList
     * automatiquement)
     */
    EnsembleDto toDto(Ensemble ensemble);

    /**
     * Méthode personnalisée pour inclure userId et rôle de l'utilisateur
     */
    default EnsembleDto toDto(Ensemble ensemble, Long userId) {
        if (ensemble == null)
            return null;

        // Appelle la méthode simple MapStruct pour copier les champs de base
        EnsembleDto dto = toDto(ensemble);

        Set<UtilisateurEnsemble> users = ensemble.getUtilisateurEnsembles();
        if (users != null && userId != null) {
            users.stream()
                    .filter(ue -> ue.getUtilisateur().getId().equals(userId))
                    .findFirst()
                    .ifPresentOrElse(ue -> {
                        dto.setUserRole(ue.getRoleDansEnsemble().name());
                        dto.setIsCreator(ue.isCreator());
                    }, () -> {
                        dto.setUserRole(null);
                        dto.setIsCreator(false);
                    });
        }

        return dto;
    }

    /**
     * Méthode pour mapper une liste d'ensembles avec MapStruct
     */
    List<EnsembleDto> toDtoList(List<Ensemble> ensembles);

    // -------------------------
    // DTO -> ENTITY
    // -------------------------

    @Mapping(target = "id", ignore = true) // L'ID ne doit jamais être modifié
    @Mapping(target = "invitations", ignore = true)
    Ensemble toEntity(EnsembleDto dto);

    // Update partiel
    @Mapping(target = "invitations", ignore = true)
    void updateEntityFromDto(EnsembleDto dto, @MappingTarget Ensemble entity);

    // -------------------------
    // Méthode utilitaire pour récupérer le créateur et le nombre de membres
    // -------------------------
    default void enrichDtoFromUsers(EnsembleDto dto, Set<UtilisateurEnsemble> users) {
        if (users == null) {
            dto.setNombreMembres(0);
            return;
        }

        dto.setNombreMembres(users.size());

        for (UtilisateurEnsemble ue : users) {
            if (ue.getRoleDansEnsemble() == Role.ADMIN) {
                dto.setCreatedBy(ue.getUtilisateur().getId());
                dto.setCreateurNom(ue.getUtilisateur().getNom());
                dto.setCreateurPrenom(ue.getUtilisateur().getPrenom());
                break; // un seul créateur
            }
        }
    }
}
