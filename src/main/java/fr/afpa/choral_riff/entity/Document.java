
package fr.afpa.choral_riff.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Représente un document musical (partition, enregistrement, etc.)
 * associé à un morceau et un utilisateur dans l'application Choral Riff.
 */
@Entity
@Table(name = "document")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_document")
    private Long id;

    @Column(name = "type", length = 100)
    private String type;

    @Column(name = "format", length = 50)
    private String format;

    @Column(name = "date_ajout")
    private LocalDate dateAjout;

    @Column(name = "url_fichier", length = 255)
    private String urlFichier;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "id_morceau", nullable = false)
    private Morceau morceau;

    // Relation Many-to-Many via DocumentInstrument
    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DocumentInstrument> documentInstruments = new HashSet<>();

    // Constructeurs
    public Document() {
    }

    public Document(String type, String format, LocalDate dateAjout, String urlFichier,
            Utilisateur utilisateur, Morceau morceau) {
        this.type = type;
        this.format = format;
        this.dateAjout = dateAjout;
        this.urlFichier = urlFichier;
        this.utilisateur = utilisateur;
        this.morceau = morceau;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public LocalDate getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(LocalDate dateAjout) {
        this.dateAjout = dateAjout;
    }

    public String getUrlFichier() {
        return urlFichier;
    }

    public void setUrlFichier(String urlFichier) {
        this.urlFichier = urlFichier;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Morceau getMorceau() {
        return morceau;
    }

    public void setMorceau(Morceau morceau) {
        this.morceau = morceau;
    }

    public Set<DocumentInstrument> getDocumentInstruments() {
        return documentInstruments;
    }

    public void setDocumentInstruments(Set<DocumentInstrument> documentInstruments) {
        this.documentInstruments = documentInstruments;
    }

    // Méthode utilitaire pour récupérer les instruments liés
    @Transient
    public Set<Instrument> getInstruments() {
        Set<Instrument> instruments = new HashSet<>();
        for (DocumentInstrument di : documentInstruments) {
            instruments.add(di.getInstrument());
        }
        return instruments;
    }

    public void addInstrument(Instrument instrument) {

        // Vérifie si l’association existe déjà
        boolean exists = documentInstruments.stream()
                .anyMatch(di -> di.getInstrument().equals(instrument));

        if (exists) {
            return; // ne rien faire si déjà lié
        }

        // Crée une nouvelle ligne dans document_instrument
        DocumentInstrument di = new DocumentInstrument(
                this,
                instrument,
                LocalDate.now());

        documentInstruments.add(di); // SEULEMENT ce côté !
    }

    // Méthode utilitaire pour supprimer un instrument
    public void removeInstrument(Instrument instrument) {
        documentInstruments.removeIf(di -> di.getInstrument().equals(instrument));
    }

}
