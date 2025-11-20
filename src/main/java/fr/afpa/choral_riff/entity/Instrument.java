package fr.afpa.choral_riff.entity;

import jakarta.persistence.*;
import java.util.Set;

/**
 * Représente un instrument musical.
 * Un instrument a un nom, peut être associé à un ensemble,
 * et peut être lié à plusieurs documents.
 */

@Entity
@Table(name = "instrument")
public class Instrument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @OneToMany(mappedBy = "instrument", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DocumentInstrument> documentInstruments;

    @ManyToOne
    @JoinColumn(name = "ensembleId")
    private Ensemble ensemble;

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

    public Ensemble getEnsemble() {
        return ensemble;
    }

    public void setEnsemble(Ensemble ensemble) {
        this.ensemble = ensemble;
    }

    public Set<DocumentInstrument> getDocumentInstruments() {
        return documentInstruments;
    }

    public void setDocumentInstruments(Set<DocumentInstrument> documentInstruments) {
        this.documentInstruments = documentInstruments;
    }

}
