package fr.afpa.choral_riff.dto;

import java.util.Set;

/**
 * DTO (Data Transfer Object) représentant un morceau de musique.
 * Utilisé pour transférer les données
 *
 * @param id          Identifiant unique du morceau.
 * @param titre       Titre du morceau.
 * @param compositeur Nom du compositeur du morceau.
 * @param genre       Genre musical du morceau (classique, jazz, Rap.).
 * @param descriptif  Description ou informations supplémentaires sur le
 *                    morceau.
 * @param ensembleId  Identifiant de l'ensemble auquel ce morceau est associé.
 * @param createurId  Identifiant de l'utilisateur qui a créé le morceau.
 * @param documents   Ensemble des documents (partitions, fichiers audio, etc.)
 *                    liés au morceau.
 */

public record MorceauDto(
                Long id,
                String titre,
                String compositeur,
                String genre,
                String descriptif,
                Long ensembleId,
                Long createurId,
                Set<DocumentDto> documents // Active aussi la validation des documents 
) {
}
