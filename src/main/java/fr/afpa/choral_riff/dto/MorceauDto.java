package fr.afpa.choral_riff.dto;

import java.util.Set;

public record MorceauDto(
        Long id,
        String titre,
        String compositeur,
        String genre,
        String descriptif,
        Long ensembleId,
        Long createurId,
        Set<DocumentDto> documents //  Active aussi la validation des documents (si tu en mets)
) {
}
