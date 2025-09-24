package fr.afpa.choral_riff.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

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

    private String etat;

    private LocalDate dateEnvoi;

    /**
     * Ensemble lié à cette invitation.
     */
    @ManyToOne
    @JoinColumn(name = "id_ensemble", nullable = false)
    private Ensemble ensemble;

    /**
     * Utilisateur à qui l'invitation est envoyée.
     */
    @ManyToOne
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur utilisateur;

    // ====== Constructeur vide par défault ======

    public Invitation() {
    }

    public Invitation(String etat, LocalDate dateEnvoi, Ensemble ensemble, Utilisateur utilisateur) {
        this.etat = etat;
        this.dateEnvoi = dateEnvoi;
        this.ensemble = ensemble;
        this.utilisateur = utilisateur;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public LocalDate getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(LocalDate dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
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
}
