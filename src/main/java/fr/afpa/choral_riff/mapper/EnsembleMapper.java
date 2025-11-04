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
    // TODO attention il n'est pas possible facilement avec l'annotation @Mapping
    // d'aller piocher l'utilisateur créateur de la liste
    // pas facile de faire "utilisateurs.id"
    // @Mapping(source = "id", target = "id")
    // EnsembleDto toDto(Ensemble ensemble);

    public default EnsembleDto toDto(Ensemble ensemble) {
        EnsembleDto ensembleDto = new EnsembleDto();
        ensembleDto.setId(ensemble.getId());
        ensembleDto.setDescription(ensemble.getDescription());
        ensembleDto.setNom(ensemble.getNom());

        // récupération de l'identifiant du créateur de l'ensembe
        Set<UtilisateurEnsemble> users = ensemble.getUtilisateurEnsembles();

    //     for (UtilisateurEnsemble user : users) {
    //         // admin == createur
    //         if (user.getRoleDansEnsemble() == Role.ADMIN) {
    //             ensembleDto.setCreatedBy(user.getId());
    //         }
    //     }
    //     return ensembleDto;
    // }



    //04/11/25
for (UtilisateurEnsemble ue : users) {
    // admin == créateur
    if (ue.getRoleDansEnsemble() == Role.ADMIN) {
        ensembleDto.setCreatedBy(ue.getUtilisateur().getId());
    }
}
return ensembleDto;
     }



    // DTO -> ENTITY
    // @Mapping(source = "id", target = "id")

    @Mapping(target = "id", ignore = true) // ID ne sera jamais modifié
    @Mapping(target = "invitations", ignore = true)
    // @Mapping(target = "createdBy", ignore = true)
    Ensemble toEntity(EnsembleDto dto);

    List<EnsembleDto> toDtoList(List<Ensemble> ensembles);

    // Update partiel
    // @Mapping(source = "id", target = "id")
    @Mapping(target = "invitations", ignore = true)
    void updateEntityFromDto(EnsembleDto dto, @MappingTarget Ensemble entity);
}
