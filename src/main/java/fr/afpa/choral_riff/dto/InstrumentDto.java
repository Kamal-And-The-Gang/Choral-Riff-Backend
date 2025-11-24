package fr.afpa.choral_riff.dto;

import java.util.Set;

/**
 * DTO (Data Transfer Object) représentant un instrument de musique.
 * Sert à transférer les données d'instrument entre les couches de
 * l'application.
 *
 * @param id         Identifiant unique de l'instrument.
 * @param nom        Nom de l'instrument (ex : Violon, Piano, Tessiture...).
 * @param ensembleId Identifiant de l'ensemble auquel l'instrument est associé.
 */

public record InstrumentDto(
                Long id,
                String nom,
                Set<Long> ensembleIds,
                Set<Long> documentIds) {
}
