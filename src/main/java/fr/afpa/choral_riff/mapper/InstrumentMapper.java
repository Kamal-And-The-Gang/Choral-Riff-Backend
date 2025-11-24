package fr.afpa.choral_riff.mapper;


import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import fr.afpa.choral_riff.dto.InstrumentDto;
import fr.afpa.choral_riff.entity.Ensemble;
import fr.afpa.choral_riff.entity.Instrument;

@Component
public class InstrumentMapper {

    // ENTITY -> DTO
    public InstrumentDto toDto(Instrument entity) {
        if (entity == null) return null;

        Set<Long> documentIds = entity.getDocuments() != null
                ? entity.getDocuments().stream().map(d -> d.getId()).collect(Collectors.toSet())
                : Set.of();

        return new InstrumentDto(
                entity.getId(),
                entity.getNom(),
                entity.getEnsembles().stream().map(e -> e.getId()).collect(Collectors.toSet()),
                documentIds
        );
    }

    // DTO -> ENTITY
    public Instrument toEntity(InstrumentDto dto) {
        if (dto == null) return null;

        Instrument instrument = new Instrument();
        instrument.setId(dto.id());
        instrument.setNom(dto.nom());

        Set<Ensemble> ensembles = new HashSet<>();
        if (dto.ensembleIds() != null) {
            dto.ensembleIds().forEach(id -> {
                Ensemble e = new Ensemble();
                e.setId(id);
                ensembles.add(e);
            });
        }
        instrument.setEnsembles(ensembles);

        // Les documents ne sont pas créés ici, juste une relation si nécessaire
        return instrument;
    }

    public void updateEntityFromDto(InstrumentDto dto, Instrument entity) {
        if (dto == null || entity == null) return;

        entity.setNom(dto.nom());

        if (dto.ensembleIds() != null) {
            Set<Ensemble> ensembles = new HashSet<>();
            dto.ensembleIds().forEach(id -> {
                Ensemble e = new Ensemble();
                e.setId(id);
                ensembles.add(e);
            });
            entity.setEnsembles(ensembles);
        }
    }
}
