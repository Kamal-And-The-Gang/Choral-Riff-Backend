
package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.dto.InstrumentDto;
import fr.afpa.choral_riff.entity.Document;
import fr.afpa.choral_riff.entity.Instrument;
import fr.afpa.choral_riff.entity.Morceau;
import fr.afpa.choral_riff.mapper.InstrumentMapper;
import fr.afpa.choral_riff.repositories.DocumentRepository;
import fr.afpa.choral_riff.repositories.EnsembleRepository;
import fr.afpa.choral_riff.repositories.InstrumentRepository;
import fr.afpa.choral_riff.repositories.MorceauRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InstrumentService {

    private final InstrumentRepository instrumentRepository;
    private final InstrumentMapper instrumentMapper;
    private final DocumentRepository documentRepository; // <-- ajouter
    private final UtilisateurEnsembleService utilisateurEnsembleService;

    private final MorceauRepository morceauRepository;

    /**
     * Injection des dépendances du service.
     * On injecte tout ce dont on a besoin : repository, mapper, service
     * utilisateur-ensemble…
     */
    public InstrumentService(InstrumentRepository instrumentRepository,
            InstrumentMapper instrumentMapper,
            UtilisateurEnsembleService utilisateurEnsembleService,
            MorceauRepository morceauRepository,
            EnsembleRepository ensembleRepository,
            DocumentRepository documentRepository) {
        this.instrumentRepository = instrumentRepository;
        this.instrumentMapper = instrumentMapper;
        this.utilisateurEnsembleService = utilisateurEnsembleService; // <-- important
        this.morceauRepository = morceauRepository;
        this.documentRepository = documentRepository;
    }

    /*
     * ============================================================
     * MÉTHODES UTILITAIRES PRIVÉES
     * ============================================================
     */

    /** Récupère un instrument ou lance une exception si introuvable */
    private Instrument getInstrumentOrThrow(Long instrumentId) {
        return instrumentRepository.findById(instrumentId)
                .orElseThrow(() -> new RuntimeException("Instrument non trouvé avec l'ID: " + instrumentId));
    }

    /*
     * ============================================================
     * MÉTHODES PUBLIQUES APPELÉES PAR LES CONTROLLERS
     * ============================================================
     */

    /** Retourne tous les instruments sous forme de DTO */
    public List<InstrumentDto> getAll() {
        return instrumentRepository.findAll().stream()
                .map(instrumentMapper::toDto)
                .toList();
    }

    /** Retourne un instrument par ID */
    public InstrumentDto getById(Long id) {
        return instrumentMapper.toDto(getInstrumentOrThrow(id));
    }

    /** Supprime un instrument si il existe */
    public void delete(Long id) {
        if (!instrumentRepository.existsById(id)) {
            throw new RuntimeException("Instrument non trouvé avec l'ID: " + id);
        }
        instrumentRepository.deleteById(id);
    }

    /**
     * Ajoute un instrument à un morceau
     * (Pour l'instant ta méthode n'a pas la relation Many-to-Many mais elle est
     * prête)
     */
    public InstrumentDto addInstrumentToMorceau(Long morceauId, Long instrumentId) {

        // 1 — Récupère le morceau
        Morceau morceau = morceauRepository.findById(morceauId)
                .orElseThrow(() -> new RuntimeException("Morceau non trouvé avec l'ID: " + morceauId));

        // 2 — Récupère l’instrument
        Instrument instrument = getInstrumentOrThrow(instrumentId);

        // Ici on pourrait faire morceau.getInstruments().add(instrument);

        // 3 — Sauvegarde
        morceauRepository.save(morceau);

        return instrumentMapper.toDto(instrument);
    }

    /**
     * Ajoute un instrument à un ensemble AVEC contrôle de rôle.
     * (ADMIN ou MODERATEUR)
     */

    public InstrumentDto update(Long id, InstrumentDto dto) {
        // Récupérer l'instrument existant
        Instrument instrument = getInstrumentOrThrow(id);

        // Mettre à jour les champs via le mapper
        instrumentMapper.updateEntityFromDto(dto, instrument);

        // Sauvegarder et retourner le DTO
        Instrument updated = instrumentRepository.save(instrument);
        return instrumentMapper.toDto(updated);
    }

    // nouvelle méthode update avec les droits

    public InstrumentDto update(Long userId, Long instrumentId, InstrumentDto dto) {
        // Récupère l’instrument
        Instrument instrument = getInstrumentOrThrow(instrumentId);

        // Récupère le document lié à l’instrument
        Document document = instrument.getDocuments().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Instrument non lié à un document"));

        // Récupère l'ensemble du morceau
        Long ensembleId = document.getMorceau().getEnsemble().getId();

        // Vérifie les droits
        if (!utilisateurEnsembleService.utilisateurAutorise(userId, ensembleId, List.of("ADMIN", "MODERATEUR"))) {
            throw new RuntimeException("Vous n'avez pas les droits pour modifier cet instrument");
        }

        // Mettre à jour les champs via le mapper
        instrumentMapper.updateEntityFromDto(dto, instrument);

        // Sauvegarder et retourner le DTO
        Instrument updated = instrumentRepository.save(instrument);
        return instrumentMapper.toDto(updated);
    }

}
