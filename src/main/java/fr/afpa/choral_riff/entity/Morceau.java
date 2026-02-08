
package fr.afpa.choral_riff.entity;

import jakarta.persistence.*;
import java.util.Set;

/**
 * Représente un morceau musical.
 * Un morceau possède un titre, un compositeur, un genre, une description,
 * appartient à un ensemble, a un créateur ou utilisateur, et peut être associé
 * à plusieurs documents et instruments.
 */

@Entity
@Table(name = "morceau")
public class Morceau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    private String compositeur;

    private String genre;

    private String descriptif;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ensembleId", nullable = false)
    private Ensemble ensemble;

    // @ManyToOne
    // @JoinColumn(name = "ensembleId")
    // private Ensemble ensemble;

    @ManyToOne
    @JoinColumn(name = "id_createur")
    private Utilisateur createur;

    @OneToMany(mappedBy = "morceau", cascade = CascadeType.ALL)
    private Set<Document> documents;

    // ===== Getters / Setters =====
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getCompositeur() {
        return compositeur;
    }

    public void setCompositeur(String compositeur) {
        this.compositeur = compositeur;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDescriptif() {
        return descriptif;
    }

    public void setDescriptif(String descriptif) {
        this.descriptif = descriptif;
    }

    public Ensemble getEnsemble() {
        return ensemble;
    }

    public void setEnsemble(Ensemble ensemble) {
        this.ensemble = ensemble;
    }

    public Utilisateur getCreateur() {
        return createur;
    }

    public void setCreateur(Utilisateur createur) {
        this.createur = createur;
    }

    public Set<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<Document> documents) {
        this.documents = documents;
    }

}
