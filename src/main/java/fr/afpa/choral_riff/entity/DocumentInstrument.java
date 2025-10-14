package fr.afpa.choral_riff.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Représente l'association entre un Document et un Instrument.
 * Cette entité modélise la relation ManyToMany entre Document et Instrument
 * avec une date d'ajout pour cette association.
 */

@Entity
@Table(name = "document_instrument", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "document_id", "instrument_id" })
})


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
