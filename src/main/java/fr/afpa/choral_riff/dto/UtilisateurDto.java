package fr.afpa.choral_riff.dto;

/**
 * DTO (Data Transfer Object) représentant un utilisateur.
 * <p>
 * Cette classe est utilisée pour transférer les données d'un utilisateur
 * entre les différentes couches de l'application.
 * </p>
 */

public class UtilisateurDto {
 
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String photoProfil;

    // Constructeur vide pour jpa
    public UtilisateurDto() {
    }
     /**
     * Constructeur avec tous les champs.
     *
     * @param id          Identifiant unique de l'utilisateur
     * @param nom         Nom de l'utilisateur
     * @param prenom      Prénom de l'utilisateur
     * @param email       Adresse email de l'utilisateur
     * @param photoProfil URL ou chemin de la photo de profil
     */

    public UtilisateurDto(Long id, String nom, String prenom, String email, String photoProfil) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.photoProfil = photoProfil;
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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoProfil() {
        return photoProfil;
    }

    public void setPhotoProfil(String photoProfil) {
        this.photoProfil = photoProfil;
    }
}
