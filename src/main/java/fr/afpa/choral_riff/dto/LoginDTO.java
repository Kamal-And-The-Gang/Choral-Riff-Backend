package fr.afpa.choral_riff.dto;

/**
 * DTO (Data Transfer Object) utilisé pour l'authentification d'un utilisateur.
 * Contient les informations nécessaires pour se connecter : email et mot de
 * passe.
 */

public class LoginDTO {
    private String email;
    private String password;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
