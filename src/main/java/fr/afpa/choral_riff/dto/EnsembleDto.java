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

        private String typeEnsemble; // <-- Nouveau champ
        // Nouveau champ pour le rôle de l’utilisateur
        private String userRole;
private String createurNom;
private String createurPrenom;
        public EnsembleDto() {
        }

      public EnsembleDto(Long id, String nom, String description, Long createdBy, String createurNom, String createurPrenom) {
    this.id = id;
    this.nom = nom;
    this.description = description;
    this.createdBy = createdBy;
    this.createurNom = createurNom;
    this.createurPrenom = createurPrenom;
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

        private boolean isCreator;

        public boolean isCreator() {
                return isCreator;
        }

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

        public String getTypeEnsemble() {
                return typeEnsemble;
        } // <-- Getter

        public void setTypeEnsemble(String typeEnsemble) {
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