
package fr.afpa.choral_riff.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import fr.afpa.choral_riff.dto.InstrumentDto;
import fr.afpa.choral_riff.entity.Instrument;

/**
 * Mapper pour convertir entre {@link Instrument} et {@link InstrumentDto}.
 * <p>
 * Fournit des méthodes pour :
 * <ul>
 * <li>Transformer une entité en DTO pour l'envoi au front-end.</li>
 * <li>Créer une entité à partir d'un DTO.</li>
 * <li>Mettre à jour partiellement une entité existante à partir d'un DTO.</li>
 * </ul>
 * </p>
 * <p>
 * Note : la relation avec les documents est convertie uniquement en
 * identifiants de documents
 * (documentIds) pour éviter les cycles de sérialisation.
 * </p>
 */

@Component
public class InstrumentMapper {

    // ENTITY -> DTO
    public InstrumentDto toDto(Instrument entity) {
        if (entity == null)
            return null;

        Set<Long> documentIds = entity.getDocuments() != null
                ? entity.getDocuments().stream()
                        .map(d -> d.getId())
                        .collect(Collectors.toSet())
                : Set.of();

        return new InstrumentDto(
                entity.getId(),
                entity.getNom(),
                documentIds // seulement documentIds maintenant
        );
    }

    /**
     * Convertit un DTO {@link InstrumentDto} en entité {@link Instrument}.
     * <p>
     * Les relations avec les documents ne sont pas gérées ici.
     * </p>
     *
     * @param dto le DTO InstrumentDto
     * @return l'entité Instrument correspondante, ou null si le DTO est null
     */

    // DTO -> ENTITY
    public Instrument toEntity(InstrumentDto dto) {
        if (dto == null)
            return null;

        Instrument instrument = new Instrument();
        instrument.setId(dto.id());
        instrument.setNom(dto.nom());

        // Aucune gestion des ensembles (tu as supprimé la relation)

        return instrument;
    }

    // UPDATE ENTITY FROM DTO
    public void updateEntityFromDto(InstrumentDto dto, Instrument entity) {
        if (dto == null || entity == null)
            return;

        entity.setNom(dto.nom());
        // Rien à mettre à jour pour les ensembles
    }
}
