package fr.afpa.choral_riff.entity;

import java.time.LocalDate;
import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;

@Entity
@Table(name = "ensemble")
public class Ensemble {

    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-incrément
    @Column(name = "ensembleId")
    private Long ensembleId;

    @Column(name = "nom", length = 255)
    private String nom;

    @Column(name = "description", length = 100)
    private String description;

    @Column(name = "date_creation")
    private LocalDate dateCreation;

    @OneToMany(mappedBy = "ensemble", cascade = CascadeType.ALL)
    private List<Invitation> invitations;

    // Constructeurs
    public Ensemble() {
    }

    public Ensemble(String nom, String description, LocalDate dateCreation) {
        this.nom = nom;
        this.description = description;
        this.dateCreation = dateCreation;
    }

    public Long getEnsembleId() {
        return ensembleId;
    }

    public void setEnsembleId(Long ensembleId) {
        this.ensembleId = ensembleId;
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

}
