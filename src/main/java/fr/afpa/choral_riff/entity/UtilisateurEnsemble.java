package fr.afpa.choral_riff.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Entité représentant la relation entre un {@link Utilisateur} et un
 * {@link Ensemble}.
 * 
 * Cette entité modélise l'appartenance d'un utilisateur à un ensemble avec un
 * rôle spécifique
 * (exemple : ADMIN, MEMBRE, Modérateur) ainsi que la date d'adhésion à cet
 * ensemble.
 * 
 * (une personne ne peut avoir qu'un seul rôle par ensemble).
 */



@Entity
@Table(name = "utilisateur_ensemble", uniqueConstraints = @UniqueConstraint(columnNames = { "utilisateur_id",
        "ensemble_id" }))
public class UtilisateurEnsemble {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // clé artificielle

    @ManyToOne
    @JsonIgnore  // Ignore la sérialisation du côté utilisateur pour éviter la boucle
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "ensemble_id", nullable = false)
    private Ensemble ensemble;

   
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role roleDansEnsemble;// utilise l'enum rôle

    @Column(nullable = false)
    private LocalDateTime dateAdhesion;

    public UtilisateurEnsemble() {
    }

    public UtilisateurEnsemble(Utilisateur utilisateur,
            Ensemble ensemble,
            Role roleDansEnsemble,
            LocalDateTime dateAdhesion) {
        this.utilisateur = utilisateur;
        this.ensemble = ensemble;
        this.roleDansEnsemble = roleDansEnsemble;
        this.dateAdhesion = dateAdhesion;
    }

    @Column(name = "is_creator", nullable = false)
    private boolean isCreator = false;
     // --- Nouvelle colonne nomComplet ---
    @Column(name = "nom_complet")
    private String nomComplet;

    // Getter et setter
    public boolean isCreator() {
        return isCreator;
    }

    public void setCreator(boolean creator) {
        isCreator = creator;
    }

    // getters et setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Ensemble getEnsemble() {
        return ensemble;
    }

    public void setEnsemble(Ensemble ensemble) {
        this.ensemble = ensemble;
    }

    public Role getRoleDansEnsemble() {
        return roleDansEnsemble;
    }

    public void setRoleDansEnsemble(Role roleDansEnsemble) {
        this.roleDansEnsemble = roleDansEnsemble;
    }

    public LocalDateTime getDateAdhesion() {
        return dateAdhesion;
    }

    public void setDateAdhesion(LocalDateTime dateAdhesion) {
        this.dateAdhesion = dateAdhesion;
    }
    public String getNomComplet() {
        return nomComplet;
    }

    public void setNomComplet(String nomComplet) {
        this.nomComplet = nomComplet;
    }
}
