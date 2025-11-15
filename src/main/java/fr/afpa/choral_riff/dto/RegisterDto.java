package fr.afpa.choral_riff.dto;
/**
 * 
 */
public class RegisterDto {
    private String email;
    private String nom;
    private String prenom;
    private String motDePasse;

    // Nouveau champ pour récupérer le token envoyé dans le lien d'invitation
    private String token;

    /**
     * @return String
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return String
     */
    public String getNom() {
        return nom;
    }

    /**
     * @param nom
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * @return String
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * @param prenom
     */
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    /**
     * @return String
     */
    public String getMotDePasse() {
        return motDePasse;
    }

    /**
     * @param motDePasse
     */
    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
