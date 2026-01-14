package fr.afpa.choral_riff.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.afpa.choral_riff.entity.TypeEnsemble;

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
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate dateCreation; // <-- ajouté
        private TypeEnsemble typeEnsemble; // <-- Nouveau champ
        // Nouveau champ pour le rôle de l’utilisateur
        private String userRole;
        private String createurNom;
        private String createurPrenom;

        public EnsembleDto() {
        }

       public EnsembleDto(
        Long id,
        String nom,
        String description,
        Long createdBy,
        String createurNom,
        String createurPrenom,
        long nombreMembres
) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.createdBy = createdBy;
        this.createurNom = createurNom;
        this.createurPrenom = createurPrenom;
        this.nombreMembres = (int) nombreMembres;
}

        // Nouveau champ
        private int nombreMembres;

        // Getters et setters
        public int getNombreMembres() {
                return nombreMembres;
        }

        public LocalDate getDateCreation() {
                return dateCreation;
        }

        public void setDateCreation(LocalDate dateCreation) {
                this.dateCreation = dateCreation;
        }

        public void setNombreMembres(int nombreMembres) {
                this.nombreMembres = nombreMembres;
        }

        public String getCreateurNom() {
                return createurNom;
        }

        public void setCreateurNom(String createurNom) {
                this.createurNom = createurNom;
        }

        public String getCreateurPrenom() {
                return createurPrenom;
        }

        public void setCreateurPrenom(String createurPrenom) {
                this.createurPrenom = createurPrenom;
        }

        public Long getId() {
                return id;
        }

        @JsonProperty("creator")
        private boolean isCreator;

        @JsonProperty("creator")
        public boolean isCreator() {
                return isCreator;
        }

        @JsonProperty("creator")
        public void setIsCreator(boolean isCreator) {
                this.isCreator = isCreator;
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

        public TypeEnsemble getTypeEnsemble() {
                return typeEnsemble;
        } // <-- Getter

        public void setTypeEnsemble(TypeEnsemble typeEnsemble) {
                this.typeEnsemble = typeEnsemble;
        } // <-- Setter

        public void setCreatedBy(Long createdBy) {
                this.createdBy = createdBy;
        }

        public String getUserRole() {
                return userRole;
        }

        public void setUserRole(String userRole) {
                this.userRole = userRole;
        }
}