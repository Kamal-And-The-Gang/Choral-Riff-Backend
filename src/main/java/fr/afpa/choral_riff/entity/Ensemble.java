package fr.afpa.choral_riff.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.OneToMany;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Représente un ensemble musical.
 * <p>
 * Un ensemble possède un nom, une description, une date de création, un type,
 * et des relations avec des invitations, des morceaux et des utilisateurs
 * associés.
 * </p>
 */

@Entity
@Table(name = "ensemble")
@JsonIgnoreProperties({ "invitations" }) // On ignore les invitations pour la sérialisation JSON
public class Ensemble {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nom", length = 255)
    private String nom;

    @Column(name = "description", length = 100)
    private String description;

    @CreationTimestamp
    @Column(name = "date_creation")
    private LocalDate dateCreation;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_ensemble", length = 50)
    private TypeEnsemble typeEnsemble;

    @Column(name = "created_by")
    private Long createdBy;

    // ===========================================
    // Relation avec Invitations
    // CascadeType.ALL supprime automatiquement toutes les invitations liées
    // orphanRemoval=true supprime les invitations orphelines
    @OneToMany(mappedBy = "ensemble", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invitation> invitations;

    // Relation avec Morceaux
    @OneToMany(mappedBy = "ensemble", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Morceau> morceaux;

    // Relation avec UtilisateurEnsemble
    @OneToMany(mappedBy = "ensemble", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UtilisateurEnsemble> utilisateurEnsembles = new HashSet<>();

   
    // ===========================================
    // Constructeurs
    public Ensemble() {
    }

    public Ensemble(String nom, String description, LocalDate dateCreation, TypeEnsemble typeEnsemble) {
        this.nom = nom;
        this.description = description;
        this.dateCreation = dateCreation;
        this.typeEnsemble = typeEnsemble;
    }

    // ===========================================
    // Getters / Setters

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public TypeEnsemble getTypeEnsemble() {
        return typeEnsemble;
    }

    public void setTypeEnsemble(TypeEnsemble typeEnsemble) {
        this.typeEnsemble = typeEnsemble;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public List<Invitation> getInvitations() {
        return invitations;
    }

    public void setInvitations(List<Invitation> invitations) {
        this.invitations = invitations;
    }

    public List<Morceau> getMorceaux() {
        return morceaux;
    }

    public void setMorceaux(List<Morceau> morceaux) {
        this.morceaux = morceaux;
    }

    public Set<UtilisateurEnsemble> getUtilisateurEnsembles() {
        return utilisateurEnsembles;
    }

    public void setUtilisateurEnsembles(Set<UtilisateurEnsemble> utilisateurEnsembles) {
        this.utilisateurEnsembles = utilisateurEnsembles;
    }
}
