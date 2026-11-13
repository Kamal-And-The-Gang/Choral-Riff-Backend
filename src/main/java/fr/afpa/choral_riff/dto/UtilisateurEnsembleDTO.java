
package fr.afpa.choral_riff.dto;

public class UtilisateurEnsembleDTO {

    private Long utilisateurId;
    private String nom;
    private String prenom;
    private String role;
    private boolean creator;
    private String photoProfil;

    public UtilisateurEnsembleDTO(
            Long utilisateurId,
            String nom,
            String prenom,
            String role,
            boolean creator,
            String photoProfil) {

        this.utilisateurId = utilisateurId;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.creator = creator;
        this.photoProfil = photoProfil;
    }

    public Long getUtilisateurId() {
        return utilisateurId;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getRole() {
        return role;
    }

    public boolean isCreator() {
        return creator;
    }

    public String getPhotoProfil() {
        return photoProfil;
    }
}
