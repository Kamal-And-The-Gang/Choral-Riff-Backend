package fr.afpa.choral_riff.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Représente l'association entre un {@link Document} et un {@link Instrument}.
 * <p>
 * Cette entité modélise la relation Many-to-Many entre Document et Instrument
 * et permet de stocker la date d'ajout de chaque association.
 * </p>
 */

@Entity
@Table(name = "document_instrument", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "document_id", "instrument_id" })
})

/**
 * Constructeur complet pour créer une association avec date.
 *
 * @param document   document associé
 * @param instrument instrument associé
 * @param dateAjout  date d'ajout de l'association
 */
public class DocumentInstrument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @ManyToOne(optional = false)
    @JoinColumn(name = "instrument_id", nullable = false)
    private Instrument instrument;

    private LocalDate dateAjout;

    // --- Constructeurs ---
    public DocumentInstrument() {
    }

    public DocumentInstrument(Document document, Instrument instrument, LocalDate dateAjout) {
        this.document = document;
        this.instrument = instrument;
        this.dateAjout = dateAjout;
    }

    /**
     * @return Long
     */
    // --- Getters / Setters ---
    public Long getId() {
        return id;
    }

    /**
     * @return Document
     */
    public Document getDocument() {
        return document;
    }

    /**
     * @param document
     */
    public void setDocument(Document document) {
        this.document = document;
    }

    /**
     * @return Instrument
     */
    public Instrument getInstrument() {
        return instrument;
    }

    /**
     * @param instrument
     */
    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    /**
     * @return LocalDate
     */
    public LocalDate getDateAjout() {
        return dateAjout;
    }

    /**
     * @param dateAjout
     */
    public void setDateAjout(LocalDate dateAjout) {
        this.dateAjout = dateAjout;
    }
}
