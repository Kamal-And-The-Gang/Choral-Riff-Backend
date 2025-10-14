package fr.afpa.choral_riff.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO utilisé pour recevoir les données nécessaires à la création d'une
 * invitation.
 */
public class CreateInvitationDTO {

    @Email(message = "L'email doit être valide")
    @NotBlank(message = "L'email est obligatoire")
    private String emailInvite;

    @NotNull(message = "L'identifiant de l'ensemble est requis")
    private Long ensembleId;

    // --- Getters & Setters ---
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
}
