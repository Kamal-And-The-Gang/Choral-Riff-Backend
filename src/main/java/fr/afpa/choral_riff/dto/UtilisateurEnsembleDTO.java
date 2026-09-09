package fr.afpa.choral_riff.dto;

public class UtilisateurEnsembleDTO {

    private Long utilisateurId;
    private String nom;
    private String prenom;
    private String role;
    private boolean creator;

    public UtilisateurEnsembleDTO(
            Long utilisateurId,
            String nom,
            String prenom,
            String role,
            boolean creator) {

        this.utilisateurId = utilisateurId;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.creator = creator;
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
}

