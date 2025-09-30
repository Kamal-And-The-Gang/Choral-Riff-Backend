package fr.afpa.choral_riff.dto;

import java.time.LocalDate;

/**
 * DTO (Data Transfer Object) représentant un ensemble musical.
 * Utilisé pour transférer les données liées à un ensemble entre les différentes
 * couches de l'application.
 *
 * @param ensembleId   Identifiant unique de l'ensemble.
 * @param nom          Nom de l'ensemble (ex : Chœur, Orchestre, Groupe, etc.).
 * @param description  Description de l'ensemble (objectif, style musical,
 *                     etc.).
 * @param dateCreation Date de création de l'ensemble.
 */

public record EnsembleDto(
        Long ensembleId,
        String nom,
        String description,
        LocalDate dateCreation) {
}