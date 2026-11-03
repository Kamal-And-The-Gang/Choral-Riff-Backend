package fr.afpa.choral_riff.dto;

import java.time.LocalDate;
/**
 * DTO pour représenter un document associé à un instrument.
 * DocumentInstrumentDto
 * @param id
 * @param documentId
 * @param instrumentId
 * @param dateAjout
 */

public record DocumentInstrumentDto(
        Long id,
        Long documentId,
        Long instrumentId,
        LocalDate dateAjout
) {}

