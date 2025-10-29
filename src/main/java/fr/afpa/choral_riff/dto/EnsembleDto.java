package fr.afpa.choral_riff.dto;

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
public class EnsembleDto {
        Long id;
        String nom;
        String description;
        Long createdBy;

        public EnsembleDto() {
        }

        public EnsembleDto(Long id, String nom, String description, Long createdBy) {
                this.id = id;
                this.nom = nom;
                this.description = description;
                this.createdBy = createdBy;
        }

        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public String getNom() {
                return nom;
        }

        public void setNom(String nom) {
                this.nom = nom;
        }

        public String getDescription() {
                return description;
        }

        public void setDescription(String description) {
                this.description = description;
        }

        public Long getCreatedBy() {
                return createdBy;
        }

        public void setCreatedBy(Long createdBy) {
                this.createdBy = createdBy;
        }
}