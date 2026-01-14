
package fr.afpa.choral_riff.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Représente un instrument musical.
 * <p>
 * Un instrument possède un nom unique et peut être associé à plusieurs
 * documents
 * via l'entité {@link DocumentInstrument}.
 * </p>
 */

@Entity
@Table(name = "instrument", uniqueConstraints = @UniqueConstraint(columnNames = "nom"))
public class Instrument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @OneToMany(mappedBy = "instrument", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DocumentInstrument> documentInstruments = new HashSet<>();

    // ===== Getters / Setters =====
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

    public Set<DocumentInstrument> getDocumentInstruments() {
        return documentInstruments;
    }

    public void setDocumentInstruments(Set<DocumentInstrument> documentInstruments) {
        this.documentInstruments = documentInstruments;
    }

    // ===== Méthode utilitaire pour récupérer directement les documents =====
    @Transient
    public Set<Document> getDocuments() {
        Set<Document> docs = new HashSet<>();
        if (documentInstruments != null) {
            documentInstruments.forEach(di -> {
                if (di.getDocument() != null) {
                    docs.add(di.getDocument());
                }
            });
        }
        return docs;
    }
}
