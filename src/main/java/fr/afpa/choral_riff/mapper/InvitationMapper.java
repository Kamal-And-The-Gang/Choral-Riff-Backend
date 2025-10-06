package fr.afpa.choral_riff.mapper;

import fr.afpa.choral_riff.dto.InvitationDTO;
import fr.afpa.choral_riff.entity.Invitation;

import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InvitationMapper {

    // ===== Entity -> DTO =====
    @Mapping(source = "ensemble.id", target = "ensembleId")
    @Mapping(source = "ensemble.nom", target = "ensembleNom")
    @Mapping(source = "utilisateur.email", target = "emailInvite") // on ne mappe que l'email
    InvitationDTO toDto(Invitation invitation);

    // ===== DTO -> Entity =====
    @Mapping(target = "ensemble", ignore = true)
    @Mapping(target = "utilisateur", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "etat", expression = "java(fr.afpa.choral_riff.entity.StatusInvitation.valueOf(dto.getEtat()))")

    Invitation toEntity(InvitationDTO dto);

    // ===== Liste Entity -> DTO =====
    List<InvitationDTO> toDtoList(List<Invitation> invitations);

    // ===== Mise à jour partielle d'une entité existante =====
    @Mapping(target = "ensemble", ignore = true)
    @Mapping(target = "utilisateur", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "etat", expression = "java(fr.afpa.choral_riff.entity.StatusInvitation.valueOf(dto.getEtat()))")
    void updateEntityFromDto(InvitationDTO dto, @MappingTarget Invitation entity);
}
