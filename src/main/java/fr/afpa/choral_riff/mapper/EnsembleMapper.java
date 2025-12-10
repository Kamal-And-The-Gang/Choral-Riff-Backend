
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

@Mapper(componentModel = "spring")
public interface EnsembleMapper {

    // ENTITY -> DTO
    default EnsembleDto toDto(Ensemble ensemble) {
        if (ensemble == null) {
            return null;
        }

        EnsembleDto ensembleDto = new EnsembleDto();
        ensembleDto.setId(ensemble.getId());
        ensembleDto.setNom(ensemble.getNom());
        ensembleDto.setDescription(ensemble.getDescription());
        ensembleDto.setTypeEnsemble(ensemble.getTypeEnsemble()); // ← nouveau champ

        // Récupération du créateur (ADMIN)
        Set<UtilisateurEnsemble> users = ensemble.getUtilisateurEnsembles();
        if (users != null) {
            for (UtilisateurEnsemble ue : users) {
                if (ue.getRoleDansEnsemble() == Role.ADMIN) {
                    ensembleDto.setCreatedBy(ue.getUtilisateur().getId());
                    ensembleDto.setCreateurNom(ue.getUtilisateur().getNom());
                    ensembleDto.setCreateurPrenom(ue.getUtilisateur().getPrenom());
                    break; // Un seul créateur
                }
            }
        }

        return ensembleDto;
    }

    // DTO -> ENTITY
    @Mapping(target = "id", ignore = true) // ID ne sera jamais modifié
    @Mapping(target = "invitations", ignore = true)
    Ensemble toEntity(EnsembleDto dto);

    List<EnsembleDto> toDtoList(List<Ensemble> ensembles);

    // Update partiel
    @Mapping(target = "invitations", ignore = true)
    void updateEntityFromDto(EnsembleDto dto, @MappingTarget Ensemble entity);
}
