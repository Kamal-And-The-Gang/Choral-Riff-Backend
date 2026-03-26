
package fr.afpa.choral_riff.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO (Data Transfer Object) représentant un document.
 * Utilisé pour transférer les données liées à un document entre les couches de
 * l'application.
 * 
 * @param id_document   Identifiant unique du document.
 * @param type          Type du document (ex : partition, enregistrement...).
 * @param format        Format du document (ex : PDF, MP3, etc.).
 * @param dateAjout     Date d'ajout du document au système.
 * @param urlFichier    URL ou chemin du fichier stocké.
 * @param utilisateurId Identifiant de l'utilisateur ayant ajouté le document.
 * @param morceauId     Identifiant du morceau associé au document.
 */

public record DocumentDto(
                Long id_document,
                String type,
                String format,
                LocalDate dateAjout,
                String urlFichier,
                String nomOriginal, // <- nouveau champ
                  
                Long utilisateurId,
                Long morceauId,
                List<InstrumentDto> instruments // <-- instruments associés
) {
}