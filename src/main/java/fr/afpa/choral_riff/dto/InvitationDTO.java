package fr.afpa.choral_riff.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) représentant une invitation envoyée à un
 * utilisateur
 * pour rejoindre un ensemble musical.
 */

public class InvitationDTO {

    private Long id;

    @Email(message = "L'email doit être valide")
    @NotBlank(message = "L'email du destinataire est obligatoire")
    private String emailInvite;

    private Long ensembleId;

    private String ensembleNom;

    private String etat;

    private LocalDateTime dateEnvoi;

    // --- Getters & Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmailInvite() {
        return emailInvite;
    }

    public void setEmailInvite(String emailInvite) {
        this.emailInvite = emailInvite;
    }

    public Long getEnsembleId() {
        return ensembleId;
    }

    public void setEnsembleId(Long ensembleId) {
        this.ensembleId = ensembleId;
    }

    public String getEnsembleNom() {
        return ensembleNom;
    }

    public void setEnsembleNom(String ensembleNom) {
        this.ensembleNom = ensembleNom;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public LocalDateTime getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(LocalDateTime dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }
}
