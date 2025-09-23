package main.java.fr.afpa.choral_riff.mapper;

import main.java.fr.afpa.choral_riff.dto.InvitationDto;
import fr.afpa.choral_riff.entity.Invitation;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InvitationMapper {

    // ===== Entity -> DTO =====
    @Mapping(source = "ensemble.idEnsemble", target = "ensembleId")
    @Mapping(source = "utilisateur.id", target = "utilisateurId")
    InvitationDto toDto(Invitation invitation);

    // ===== DTO -> Entity =====
    @Mapping(target = "ensemble", ignore = true)
    @Mapping(target = "utilisateur", ignore = true)
    @Mapping(target = "id", ignore = true)
    Invitation toEntity(InvitationDto dto);

    // ===== Liste Entity -> DTO =====
    List<InvitationDto> toDtoList(List<Invitation> invitations);

    // ===== Mise à jour partielle d'une entité existante =====
    @Mapping(target = "ensemble", ignore = true)
    @Mapping(target = "utilisateur", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(InvitationDto dto, @MappingTarget Invitation entity);
}
