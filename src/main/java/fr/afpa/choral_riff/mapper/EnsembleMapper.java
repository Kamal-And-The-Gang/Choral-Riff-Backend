
// package fr.afpa.choral_riff.mapper;

// import fr.afpa.choral_riff.dto.EnsembleDto;
// import fr.afpa.choral_riff.entity.Ensemble;
// import fr.afpa.choral_riff.entity.Role;
// import fr.afpa.choral_riff.entity.UtilisateurEnsemble;

// import org.mapstruct.Mapper;
// import org.mapstruct.Mapping;
// import org.mapstruct.MappingTarget;

// import java.util.List;
// import java.util.Set;

// /**
//  * Mapper MapStruct pour convertir entre {@link Ensemble} et
//  * {@link EnsembleDto}.
//  * <p>
//  * Gère également l'extraction des informations du créateur de l'ensemble
//  * en se basant sur le rôle {@link Role#ADMIN}.
//  * </p>
//  * <p>
//  * Permet de transformer les entités pour le front-end et de mettre à jour
//  * partiellement les entités existantes à partir d'un DTO.
//  * </p>
//  */

// @Mapper(componentModel = "spring")
// public interface EnsembleMapper {

//     // ENTITY -> DTO
//     // default EnsembleDto toDto(Ensemble ensemble) {
//     // if (ensemble == null) {
//     // return null;
//     // }

//     // EnsembleDto ensembleDto = new EnsembleDto();
//     // ensembleDto.setId(ensemble.getId());
//     // ensembleDto.setNom(ensemble.getNom());
//     // ensembleDto.setDescription(ensemble.getDescription());
//     // ensembleDto.setTypeEnsemble(ensemble.getTypeEnsemble()); // ← nouveau champ
//     // // Date de création
//     // ensembleDto.setDateCreation(ensemble.getDateCreation());
//     // // Récupération du créateur (ADMIN)
//     // Set<UtilisateurEnsemble> users = ensemble.getUtilisateurEnsembles();
//     // if (users != null) {
//     // for (UtilisateurEnsemble ue : users) {
//     // if (ue.getRoleDansEnsemble() == Role.ADMIN) {
//     // ensembleDto.setCreatedBy(ue.getUtilisateur().getId());
//     // ensembleDto.setCreateurNom(ue.getUtilisateur().getNom());
//     // ensembleDto.setCreateurPrenom(ue.getUtilisateur().getPrenom());
//     // break; // Un seul créateur
//     // }
//     // }
//     // // Nombre de membres
//     // ensembleDto.setNombreMembres(users.size());
//     // } else {
//     // ensembleDto.setNombreMembres(0);
//     // }

//     // return ensembleDto;
//     // }

//     default EnsembleDto toDto(Ensemble ensemble, Long userId) {
//         if (ensemble == null)
//             return null;

//         EnsembleDto ensembleDto = new EnsembleDto();
//         ensembleDto.setId(ensemble.getId());
//         ensembleDto.setNom(ensemble.getNom());
//         ensembleDto.setDescription(ensemble.getDescription());
//         ensembleDto.setTypeEnsemble(ensemble.getTypeEnsemble());
//         ensembleDto.setDateCreation(ensemble.getDateCreation());

//         Set<UtilisateurEnsemble> users = ensemble.getUtilisateurEnsembles();
//         if (users != null) {
//             for (UtilisateurEnsemble ue : users) {
//                 if (ue.getRoleDansEnsemble() == Role.ADMIN) {
//                     ensembleDto.setCreatedBy(ue.getUtilisateur().getId());
//                     ensembleDto.setCreateurNom(ue.getUtilisateur().getNom());
//                     ensembleDto.setCreateurPrenom(ue.getUtilisateur().getPrenom());
//                     break;
//                 }
//             }
//             ensembleDto.setNombreMembres(users.size());
//         } else {
//             ensembleDto.setNombreMembres(0);
//         }

//         // Rôle et creator obligatoires
//         if (userId == null) {
//             throw new IllegalArgumentException("userId est obligatoire pour construire le DTO");
//         }
//         users.stream()
//                 .filter(ue -> ue.getUtilisateur().getId().equals(userId))
//                 .findFirst()
//                 .ifPresentOrElse(ue -> {
//                     ensembleDto.setUserRole(ue.getRoleDansEnsemble().name());
//                     ensembleDto.setIsCreator(ue.isCreator());
//                 }, () -> {
//                     ensembleDto.setUserRole(null);
//                     ensembleDto.setIsCreator(false);
//                 });

//         return ensembleDto;
//     }

//     /**
//      * Convertit un DTO {@link EnsembleDto} en entité {@link Ensemble}.
//      * <p>
//      * Les invitations sont ignorées pour éviter les cycles ou conflits lors de la
//      * sérialisation.
//      * L'ID de l'entité est également ignoré car il ne doit pas être modifié.
//      * </p>
//      *
//      * @param dto le DTO EnsembleDto
//      * @return l'entité Ensemble correspondante
//      */

//     // DTO -> ENTITY
//     @Mapping(target = "id", ignore = true) // ID ne sera jamais modifié
//     @Mapping(target = "invitations", ignore = true)
//     Ensemble toEntity(EnsembleDto dto);

//     List<EnsembleDto> toDtoList(List<Ensemble> ensembles);

//     // Update partiel
//     @Mapping(target = "invitations", ignore = true)
//     void updateEntityFromDto(EnsembleDto dto, @MappingTarget Ensemble entity);
// }
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
 * Mapper MapStruct pour convertir entre {@link Ensemble} et {@link EnsembleDto}.
 * Gère également l'extraction des informations du créateur de l'ensemble.
 */
@Mapper(componentModel = "spring")
public interface EnsembleMapper {

    // -------------------------
    // ENTITY -> DTO
    // -------------------------

    /**
     * Méthode simple pour MapStruct (utilisée pour générer toDtoList automatiquement)
     */
    EnsembleDto toDto(Ensemble ensemble);

    /**
     * Méthode personnalisée pour inclure userId et rôle de l'utilisateur
     */
    default EnsembleDto toDto(Ensemble ensemble, Long userId) {
        if (ensemble == null) return null;

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
