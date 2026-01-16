package fr.afpa.choral_riff.dto;

import java.time.LocalDateTime;

public class NotificationDto {

    private Long id;
    private String type; // "INVITATION", "MORCEAU_AJOUTE", "GENERAL"
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;

    // Contexte
    private Long ensembleId;
    private String ensembleNom;
private String morceauTitre;

    // Spécifique à l'invitation
    private Long invitationId;
    private String status; 
    private String senderName;

    // Pour accepter/refuser via backend
    private String token;
 // ← NOUVEAU CHAMP
    private Long utilisateurId;

    // Getters et setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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

    public Long getInvitationId() {
        return invitationId;
    }

    public void setInvitationId(Long invitationId) {
        this.invitationId = invitationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMorceauTitre() {
    return morceauTitre;
}

public void setMorceauTitre(String morceauTitre) {
    this.morceauTitre = morceauTitre;
}

    // ← GETTER/SETTER pour utilisateurId
    public Long getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(Long utilisateurId) { this.utilisateurId = utilisateurId; }
}
