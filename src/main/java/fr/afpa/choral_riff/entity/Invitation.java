package fr.afpa.choral_riff.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * Entité représentant une invitation envoyée à un utilisateur pour rejoindre un
 * ensemble.
 */
@Entity
@Table(name = "invitation")
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private StatusInvitation etat;

    // private LocalDate dateEnvoi;

    // @Column(unique = true, nullable = true)
    // private String token; // Token unique d'invitation

    private LocalDateTime dateEnvoi;

    @OneToMany(mappedBy = "invitation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications;

    private LocalDateTime dateExpiration; // <-- AJOUT ICI

    @Column(unique = true, nullable = true)
    private String token;

    /**
     * Ensemble lié à cette invitation.
     */
    @ManyToOne
    @JoinColumn(name = "ensembleId", nullable = false)
    @JsonBackReference // Cela évite de sérialiser l'Ensemble à nouveau (récursivité)
    private Ensemble ensemble;

    /**
     * Utilisateur à qui l'invitation est envoyée.
     */
    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;// nullable

    @Column(name = "email_invite", nullable = false)
    private String emailInvite;

    // ====== Constructeur vide par défault ======

    public Invitation() {
    }

    public Invitation(StatusInvitation etat, LocalDateTime dateEnvoi, String token, Ensemble ensemble,
            Utilisateur utilisateur) {
        this.etat = etat;
        this.dateEnvoi = dateEnvoi;
        this.token = token;
        this.ensemble = ensemble;
        this.utilisateur = utilisateur;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StatusInvitation getEtat() {
        return etat;
    }

    public void setEtat(StatusInvitation etat) {
        this.etat = etat;
    }

    public LocalDateTime getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(LocalDateTime dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Ensemble getEnsemble() {
        return ensemble;
    }

    public void setEnsemble(Ensemble ensemble) {
        this.ensemble = ensemble;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public String getEmailInvite() {
        return emailInvite;
    }

    public void setEmailInvite(String emailInvite) {
        this.emailInvite = emailInvite;
    }

    public LocalDateTime getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(LocalDateTime dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    // Champ pour savoir si le token a déjà été utilisé
    private LocalDateTime dateUtilisation;

    // Méthode utilitaire simple (optionnel mais pratique)
    public boolean isUsed() {
        return dateUtilisation != null;
    }


    public LocalDateTime getDateUtilisation() {
    return dateUtilisation;
}

public void setDateUtilisation(LocalDateTime dateUtilisation) {
    this.dateUtilisation = dateUtilisation;
}


}
