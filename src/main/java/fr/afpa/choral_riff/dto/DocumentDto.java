
package fr.afpa.choral_riff.dto;

import java.time.LocalDate;

public record DocumentDto(
    Long id_document,
    String type,
    String format,
    LocalDate dateAjout,
    String urlFichier,
    Long utilisateurId,
    Long morceauId
) {}