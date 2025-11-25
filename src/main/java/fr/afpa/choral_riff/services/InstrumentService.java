// // package fr.afpa.choral_riff.services;

// // import fr.afpa.choral_riff.dto.InstrumentDto;
// // import fr.afpa.choral_riff.entity.Ensemble;
// // import fr.afpa.choral_riff.entity.Instrument;
// // import fr.afpa.choral_riff.mapper.InstrumentMapper;
// // import fr.afpa.choral_riff.repositories.EnsembleRepository;
// // import fr.afpa.choral_riff.repositories.InstrumentRepository;
// // import org.springframework.stereotype.Service;
// // import java.util.List;
// // import java.util.stream.Collectors;
// // import fr.afpa.choral_riff.repositories.UtilisateurEnsembleRepository;

// // @Service
// // public class InstrumentService {

// //     private final InstrumentRepository instrumentRepository;
// //     private final EnsembleRepository ensembleRepository;
// //     private final InstrumentMapper instrumentMapper;
// //     private final UtilisateurEnsembleService utilisateurEnsembleService;

// //     public InstrumentService(InstrumentRepository instrumentRepository,
// //             EnsembleRepository ensembleRepository,
// //             InstrumentMapper instrumentMapper,  UtilisateurEnsembleService utilisateurEnsembleService) {
// //         this.instrumentRepository = instrumentRepository;
// //         this.ensembleRepository = ensembleRepository;
// //         this.instrumentMapper = instrumentMapper;
// //         this.utilisateurEnsembleService = utilisateurEnsembleService;
// //     }

// //     // Récupérer tous les instruments
// //     public List<InstrumentDto> getAll() {
// //         return instrumentRepository.findAll().stream()
// //                 .map(instrumentMapper::toDto)
// //                 .collect(Collectors.toList());
// //     }

// //     // Récupérer un instrument par ID
// //     public InstrumentDto getById(Long id) {
// //         Instrument instrument = instrumentRepository.findById(id)
// //                 .orElseThrow(() -> new RuntimeException("Instrument non trouvé avec l'ID: " + id));
// //         return instrumentMapper.toDto(instrument);
// //     }

// //     // Créer un instrument avec validation de l’ensemble
// //     public InstrumentDto create(InstrumentDto dto, Long ensembleId) {
// //         Instrument instrument = instrumentMapper.toEntity(dto);

// //         if (ensembleId != null) {
// //             Ensemble ensemble = ensembleRepository.findById(ensembleId)
// //                     .orElseThrow(() -> new RuntimeException("Ensemble non trouvé avec l'ID: " + ensembleId));
// //             instrument.setEnsemble(ensemble);
// //         }

// //         Instrument saved = instrumentRepository.save(instrument);
// //         return instrumentMapper.toDto(saved);
// //     }

// //     // Mettre à jour un instrument existant
// //     public InstrumentDto update(Long id, InstrumentDto dto, Long ensembleId) {
// //         Instrument instrument = instrumentRepository.findById(id)
// //                 .orElseThrow(() -> new RuntimeException("Instrument non trouvé avec l'ID: " + id));

// //         instrumentMapper.updateEntityFromDto(dto, instrument);

// //         if (ensembleId != null) {
// //             Ensemble ensemble = ensembleRepository.findById(ensembleId)
// //                     .orElseThrow(() -> new RuntimeException("Ensemble non trouvé avec l'ID: " + ensembleId));
// //             instrument.setEnsemble(ensemble);
// //         }

// //         Instrument updated = instrumentRepository.save(instrument);
// //         return instrumentMapper.toDto(updated);
// //     }

// //     // Supprimer un instrument par ID
// //     public void delete(Long id) {
// //         if (!instrumentRepository.existsById(id)) {
// //             throw new RuntimeException("Instrument non trouvé avec l'ID: " + id);
// //         }
// //         instrumentRepository.deleteById(id);
// //     }

// //     // Ajouter un instrument à un ensemble avec contrôle de rôle
// //     public InstrumentDto addInstrumentToEnsemble(Long ensembleId, Long utilisateurId, InstrumentDto dto) {

// //         //  Vérification des droits
// //         if (!utilisateurEnsembleService.utilisateurAutorise(utilisateurId, ensembleId,
// //                 List.of("ADMIN", "MODERATEUR"))) {
// //             throw new RuntimeException("Vous n'êtes pas autorisé à ajouter un instrument à cet ensemble");
// //         }

// //         Ensemble ensemble = ensembleRepository.findById(ensembleId)
// //                 .orElseThrow(() -> new RuntimeException("Ensemble non trouvé avec l'ID: " + ensembleId));

// //         Instrument instrument = instrumentMapper.toEntity(dto);
// //         instrument.setEnsemble(ensemble);

// //         return instrumentMapper.toDto(instrumentRepository.save(instrument));
// //     }

// // }

// package fr.afpa.choral_riff.services;

// import fr.afpa.choral_riff.dto.InstrumentDto;
// import fr.afpa.choral_riff.entity.Ensemble;
// import fr.afpa.choral_riff.entity.Instrument;
// import fr.afpa.choral_riff.entity.Morceau;
// import fr.afpa.choral_riff.mapper.InstrumentMapper;
// import fr.afpa.choral_riff.repositories.EnsembleRepository;
// import fr.afpa.choral_riff.repositories.InstrumentRepository;
// import fr.afpa.choral_riff.repositories.MorceauRepository;

// import org.springframework.stereotype.Service;

// import java.util.HashSet;
// import java.util.List;
// import java.util.Set;
// import java.util.stream.Collectors;

// @Service
// public class InstrumentService {

//     private final InstrumentRepository instrumentRepository;
//     private final EnsembleRepository ensembleRepository;
//     private final InstrumentMapper instrumentMapper;
//     private final UtilisateurEnsembleService utilisateurEnsembleService;
//     private final MorceauRepository morceauRepository; // ou DocumentInstrumentRepository

//     public InstrumentService(InstrumentRepository instrumentRepository,
//             EnsembleRepository ensembleRepository,
//             InstrumentMapper instrumentMapper,
//             UtilisateurEnsembleService utilisateurEnsembleService,
//             MorceauRepository morceauRepository) {
//         this.instrumentRepository = instrumentRepository;
//         this.ensembleRepository = ensembleRepository;
//         this.instrumentMapper = instrumentMapper;
//         this.utilisateurEnsembleService = utilisateurEnsembleService;
//         this.morceauRepository = morceauRepository;
//     }

//     // Récupérer tous les instruments
//     public List<InstrumentDto> getAll() {
//         return instrumentRepository.findAll().stream()
//                 .map(instrumentMapper::toDto)
//                 .collect(Collectors.toList());
//     }

//     // Récupérer un instrument par ID
//     public InstrumentDto getById(Long id) {
//         Instrument instrument = instrumentRepository.findById(id)
//                 .orElseThrow(() -> new RuntimeException("Instrument non trouvé avec l'ID: " + id));
//         return instrumentMapper.toDto(instrument);
//     }

//     // Créer un instrument avec validation de l’ensemble
//     public InstrumentDto create(InstrumentDto dto, Long ensembleId) {
//         // Vérifier si un instrument avec le même nom existe déjà
//         Instrument instrument = instrumentRepository.findByNom(dto.nom())
//                 .orElseGet(() -> instrumentMapper.toEntity(dto));

//         if (ensembleId != null) {
//             Ensemble ensemble = ensembleRepository.findById(ensembleId)
//                     .orElseThrow(() -> new RuntimeException("Ensemble non trouvé avec l'ID: " + ensembleId));
//             Set<Ensemble> ensembles = instrument.getEnsembles();
//             if (ensembles == null)
//                 ensembles = new HashSet<>();
//             ensembles.add(ensemble);
//             instrument.setEnsembles(ensembles);
//         }

//         Instrument saved = instrumentRepository.save(instrument);
//         return instrumentMapper.toDto(saved);
//     }

//     // Mettre à jour un instrument existant
//     public InstrumentDto update(Long id, InstrumentDto dto, Long ensembleId) {
//         Instrument instrument = instrumentRepository.findById(id)
//                 .orElseThrow(() -> new RuntimeException("Instrument non trouvé avec l'ID: " + id));

//         instrumentMapper.updateEntityFromDto(dto, instrument);

//         if (ensembleId != null) {
//             Ensemble ensemble = ensembleRepository.findById(ensembleId)
//                     .orElseThrow(() -> new RuntimeException("Ensemble non trouvé avec l'ID: " + ensembleId));

//             // Many-to-Many
//             Set<Ensemble> ensembles = instrument.getEnsembles();
//             if (ensembles == null) {
//                 ensembles = new HashSet<>();
//             }
//             ensembles.add(ensemble);
//             instrument.setEnsembles(ensembles);
//         }

//         Instrument updated = instrumentRepository.save(instrument);
//         return instrumentMapper.toDto(updated);
//     }

//     // Supprimer un instrument par ID
//     public void delete(Long id) {
//         if (!instrumentRepository.existsById(id)) {
//             throw new RuntimeException("Instrument non trouvé avec l'ID: " + id);
//         }
//         instrumentRepository.deleteById(id);
//     }

//     // Dans InstrumentService.java
//     public InstrumentDto addInstrumentToMorceau(Long morceauId, Long instrumentId) {

//         // 1 Récupérer le morceau
//         Morceau morceau = morceauRepository.findById(morceauId)
//                 .orElseThrow(() -> new RuntimeException("Morceau non trouvé avec l'ID: " + morceauId));

//         // 2 Récupérer l'instrument
//         Instrument instrument = instrumentRepository.findById(instrumentId)
//                 .orElseThrow(() -> new RuntimeException("Instrument non trouvé avec l'ID: " + instrumentId));

//         // 4 Sauvegarder le morceau
//         morceauRepository.save(morceau);

//         // 5 Retourner le DTO de l'instrument (ou du morceau selon ton besoin)
//         return instrumentMapper.toDto(instrument);
//     }

//     public InstrumentDto addInstrumentToEnsemble(Long ensembleId, Long utilisateurId, InstrumentDto dto) {

//     // 1 — Vérifier les droits
//     if (!utilisateurEnsembleService.utilisateurAutorise(
//             utilisateurId,
//             ensembleId,
//             List.of("ADMIN", "MODERATEUR")
//     )) {
//         throw new RuntimeException("Vous n'êtes pas autorisé à ajouter un instrument à cet ensemble");
//     }

//     // 2 — Récupération de l’ensemble
//     Ensemble ensemble = ensembleRepository.findById(ensembleId)
//             .orElseThrow(() -> new RuntimeException("Ensemble non trouvé avec l'ID: " + ensembleId));

//     // 3 — Création ou récupération de l’instrument
//     Instrument instrument = instrumentRepository.findByNom(dto.nom())
//             .orElseGet(() -> instrumentMapper.toEntity(dto));

//     // 4 — Ajout du lien Many-to-Many
//     Set<Ensemble> ensembles = instrument.getEnsembles();
//     if (ensembles == null) {
//         ensembles = new HashSet<>();
//     }
//     ensembles.add(ensemble);
//     instrument.setEnsembles(ensembles);

//     // 5 — Sauvegarde
//     Instrument saved = instrumentRepository.save(instrument);

//     return instrumentMapper.toDto(saved);
// }


// }
package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.dto.InstrumentDto;
import fr.afpa.choral_riff.entity.Ensemble;
import fr.afpa.choral_riff.entity.Instrument;
import fr.afpa.choral_riff.entity.Morceau;
import fr.afpa.choral_riff.mapper.InstrumentMapper;
import fr.afpa.choral_riff.repositories.EnsembleRepository;
import fr.afpa.choral_riff.repositories.InstrumentRepository;
import fr.afpa.choral_riff.repositories.MorceauRepository;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;


@Service
public class InstrumentService {

    private final InstrumentRepository instrumentRepository;
    private final EnsembleRepository ensembleRepository;
    private final InstrumentMapper instrumentMapper;
    private final UtilisateurEnsembleService utilisateurEnsembleService;
    private final MorceauRepository morceauRepository;

    /**
     * Injection des dépendances du service.
     * On injecte tout ce dont on a besoin : repository, mapper, service utilisateur-ensemble…
     */
    public InstrumentService(InstrumentRepository instrumentRepository,
                             EnsembleRepository ensembleRepository,
                             InstrumentMapper instrumentMapper,
                             UtilisateurEnsembleService utilisateurEnsembleService,
                             MorceauRepository morceauRepository) {
        this.instrumentRepository = instrumentRepository;
        this.ensembleRepository = ensembleRepository;
        this.instrumentMapper = instrumentMapper;
        this.utilisateurEnsembleService = utilisateurEnsembleService;
        this.morceauRepository = morceauRepository;
    }

    /* ============================================================
       MÉTHODES UTILITAIRES PRIVÉES
       ============================================================ */

    /** Récupère un ensemble ou lance une exception si introuvable */
    private Ensemble getEnsembleOrThrow(Long ensembleId) {
        return ensembleRepository.findById(ensembleId)
                .orElseThrow(() -> new RuntimeException("Ensemble non trouvé avec l'ID: " + ensembleId));
    }

    /** Récupère un instrument ou lance une exception si introuvable */
    private Instrument getInstrumentOrThrow(Long instrumentId) {
        return instrumentRepository.findById(instrumentId)
                .orElseThrow(() -> new RuntimeException("Instrument non trouvé avec l'ID: " + instrumentId));
    }

    /**
     * Récupère un instrument par nom OU crée un nouvel instrument
     * → utile pour éviter les doublons d’instruments
     */
    private Instrument getOrCreateInstrument(InstrumentDto dto) {
        return instrumentRepository.findByNom(dto.nom())
                .orElseGet(() -> instrumentMapper.toEntity(dto));
    }

    /**
     * Ajoute un ensemble dans la liste Many-to-Many d’un instrument.
     * Si la liste est null → on crée une HashSet.
     */
    private void addEnsembleToInstrument(Instrument instrument, Ensemble ensemble) {
        if (instrument.getEnsembles() == null) {
            instrument.setEnsembles(new HashSet<>());
        }
        instrument.getEnsembles().add(ensemble);
    }

    /**
     * Vérifie que l'utilisateur a les rôles nécessaires :
     * ADMIN ou MODERATEUR.
     */
    private void checkPermission(Long utilisateurId, Long ensembleId) {
        if (!utilisateurEnsembleService.utilisateurAutorise(
                utilisateurId,
                ensembleId,
                List.of("ADMIN", "MODERATEUR")
        )) {
            throw new RuntimeException("Vous n'êtes pas autorisé à ajouter un instrument à cet ensemble");
        }
    }

    /* ============================================================
       MÉTHODES PUBLIQUES APPELÉES PAR LES CONTROLLERS
       ============================================================ */

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

    /**
     * Création d’un instrument.
     * Si ensembleId est fourni → on rattache l’instrument à l’ensemble.
     */
    public InstrumentDto create(InstrumentDto dto, Long ensembleId) {

        // 1 — Récupère ou crée l’instrument selon si le nom existe déjà
        Instrument instrument = getOrCreateInstrument(dto);

        // 2 — Si un ensembleId est passé, on rattache à l’ensemble
        if (ensembleId != null) {
            Ensemble ensemble = getEnsembleOrThrow(ensembleId);
            addEnsembleToInstrument(instrument, ensemble);
        }

        // 3 — Sauvegarde
        return instrumentMapper.toDto(instrumentRepository.save(instrument));
    }

    /**
     * Mise à jour d’un instrument existant.
     * On modifie ses champs, et éventuellement son ensemble.
     */
    public InstrumentDto update(Long id, InstrumentDto dto, Long ensembleId) {

        // 1 — Récupérer l'instrument existant
        Instrument instrument = getInstrumentOrThrow(id);

        // 2 — Mettre à jour les champs (mapper)
        instrumentMapper.updateEntityFromDto(dto, instrument);

        // 3 — Ajouter un ensemble si fourni
        if (ensembleId != null) {
            Ensemble ensemble = getEnsembleOrThrow(ensembleId);
            addEnsembleToInstrument(instrument, ensemble);
        }

        // 4 — Sauvegarde
        return instrumentMapper.toDto(instrumentRepository.save(instrument));
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
     * (Pour l'instant ta méthode n'a pas la relation Many-to-Many mais elle est prête)
     */
    public InstrumentDto addInstrumentToMorceau(Long morceauId, Long instrumentId) {

        // 1 — Récupère le morceau
        Morceau morceau = morceauRepository.findById(morceauId)
                .orElseThrow(() -> new RuntimeException("Morceau non trouvé avec l'ID: " + morceauId));

        // 2 — Récupère l’instrument
        Instrument instrument = getInstrumentOrThrow(instrumentId);

        // Ici tu pourrais faire morceau.getInstruments().add(instrument);

        // 3 — Sauvegarde
        morceauRepository.save(morceau);

        return instrumentMapper.toDto(instrument);
    }

    /**
     * Ajoute un instrument à un ensemble AVEC contrôle de rôle.
     * (ADMIN ou MODERATEUR)
     */
    public InstrumentDto addInstrumentToEnsemble(Long ensembleId, Long utilisateurId, InstrumentDto dto) {

        // 1 — Vérifier les droits
        checkPermission(utilisateurId, ensembleId);

        // 2 — Récupérer l’ensemble
        Ensemble ensemble = getEnsembleOrThrow(ensembleId);

        // 3 — Récupérer ou créer l’instrument
        Instrument instrument = getOrCreateInstrument(dto);

        // 4 — Ajouter le lien Many-to-Many
        addEnsembleToInstrument(instrument, ensemble);

        // 5 — Sauvegarde
        return instrumentMapper.toDto(instrumentRepository.save(instrument));
    }
}

