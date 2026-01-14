
package fr.afpa.choral_riff.mapper;

import fr.afpa.choral_riff.dto.InvitationDTO;
import fr.afpa.choral_riff.entity.Invitation;
import fr.afpa.choral_riff.entity.StatusInvitation;
import org.mapstruct.*;
import java.util.List;

/**
 * Mapper pour convertir entre {@link Invitation} et {@link InvitationDTO}.
 * <p>
 * Fournit des méthodes pour :
 * <ul>
 * <li>Transformer une entité Invitation en DTO pour le front-end.</li>
 * <li>Créer une entité Invitation à partir d'un DTO.</li>
 * <li>Mettre à jour partiellement une entité existante à partir d'un DTO.</li>
 * <li>Gérer la conversion spécifique de l'état de l'invitation
 * (StatusInvitation).</li>
 * </ul>
 * </p>
 */

@Mapper(componentModel = "spring")
public interface InvitationMapper {

    @Mapping(source = "ensemble.id", target = "ensembleId")
    @Mapping(source = "ensemble.nom", target = "ensembleNom")
    @Mapping(source = "emailInvite", target = "emailInvite")
    @Mapping(target = "etat", expression = "java(invitation.getEtat() != null ? invitation.getEtat().name() : null)")
    InvitationDTO toDto(Invitation invitation);

    // DTO -> Entity
    @Mapping(target = "ensemble", ignore = true)
    @Mapping(target = "utilisateur", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "etat", expression = "java(mapEtat(dto.getEtat()))")
    Invitation toEntity(InvitationDTO dto);

    List<InvitationDTO> toDtoList(List<Invitation> invitations);

    void updateEntityFromDto(InvitationDTO dto, @MappingTarget Invitation entity);

    default StatusInvitation mapEtat(String etat) {
        if (etat == null)
            return StatusInvitation.EN_ATTENTE;
        try {
            return StatusInvitation.valueOf(etat);
        } catch (IllegalArgumentException e) {
            return StatusInvitation.EN_ATTENTE;
        }
    }

    @AfterMapping
    default void afterMapping(Invitation invitation, @MappingTarget InvitationDTO dto) {
        if (invitation.getUtilisateur() != null) {
            dto.setExistant(true);
            dto.setUtilisateurNom(invitation.getUtilisateur().getNom());
        } else {
            dto.setExistant(false);
        }
    }

    // --- Conversion Long <-> Invitation pour Notification ---
    default Invitation map(Long id) {
        if (id == null)
            return null;
        Invitation invitation = new Invitation();
        invitation.setId(id);
        return invitation;
    }

    default Long map(Invitation invitation) {
        return invitation != null ? invitation.getId() : null;
    }
}
