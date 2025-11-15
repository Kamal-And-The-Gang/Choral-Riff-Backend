package fr.afpa.choral_riff.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;

import org.hibernate.annotations.CreationTimestamp;

/**
 * Représente un ensemble musical.
 * Un ensemble possède un nom, une description, une date de création,
 * et une liste d'invitations associées.
 */

@Entity
@Table(name = "ensemble")
public class Ensemble {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-incrément
    @Column(name = "id")
    private Long id;

    @Column(name = "nom", length = 255)
    private String nom;

    @Column(name = "description", length = 100)
    private String description;

    @CreationTimestamp
    @Column(name = "date_creation")
    private LocalDate dateCreation;

    @OneToMany(mappedBy = "ensemble", cascade = CascadeType.ALL)
    private List<Invitation> invitations;

    @OneToMany(mappedBy = "ensemble", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UtilisateurEnsemble> utilisateurEnsembles = new HashSet<>();

    // Constructeurs
    public Ensemble() {
    }

    public Ensemble(String nom, String description, LocalDate dateCreation) {
        this.nom = nom;
        this.description = description;
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

    public List<Invitation> getInvitations() {
        return invitations;
    }

    public void setInvitations(List<Invitation> invitations) {
        this.invitations = invitations;
    }

    // Getters et setters
    public Set<UtilisateurEnsemble> getUtilisateurEnsembles() {
        return utilisateurEnsembles;
    }

    public void setUtilisateurEnsembles(Set<UtilisateurEnsemble> utilisateurEnsembles) {
        this.utilisateurEnsembles = utilisateurEnsembles;
    }

}
