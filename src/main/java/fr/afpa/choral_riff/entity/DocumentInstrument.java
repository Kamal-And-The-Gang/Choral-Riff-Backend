package main.java.fr.afpa.choral_riff.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

import fr.afpa.choral_riff.entity.Document;

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

    // --- Getters / Setters ---
    public Long getId() {
        return id;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public LocalDate getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(LocalDate dateAjout) {
        this.dateAjout = dateAjout;
    }
}
