package fr.afpa.choral_riff.mapper;

import fr.afpa.choral_riff.dto.CreateInvitationDTO;
import fr.afpa.choral_riff.entity.Invitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")

public interface CreateInvitationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "emailInvite", source = "emailInvite")
    // L'ensemble sera rattaché manuellement dans le service
    @Mapping(target = "ensemble", ignore = true)
    @Mapping(target = "etat", ignore = true)
    @Mapping(target = "dateEnvoi", ignore = true)
    @Mapping(target = "token", ignore = true)
    Invitation toEntity(CreateInvitationDTO dto);

    // Optionnel si besoin d’un DTO pour retour — sinon on peut utiliser InvitationDTO
    CreateInvitationDTO toDto(Invitation invitation);
}
