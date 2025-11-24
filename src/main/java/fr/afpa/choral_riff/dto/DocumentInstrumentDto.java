package fr.afpa.choral_riff.dto;

import java.time.LocalDate;

public record DocumentInstrumentDto(
        Long id,
        Long documentId,
        Long instrumentId,
        LocalDate dateAjout
) {}
