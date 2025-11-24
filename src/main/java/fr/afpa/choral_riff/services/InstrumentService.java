// package fr.afpa.choral_riff.services;

// import fr.afpa.choral_riff.dto.InstrumentDto;
// import fr.afpa.choral_riff.entity.Ensemble;
// import fr.afpa.choral_riff.entity.Instrument;
// import fr.afpa.choral_riff.mapper.InstrumentMapper;
// import fr.afpa.choral_riff.repositories.EnsembleRepository;
// import fr.afpa.choral_riff.repositories.InstrumentRepository;
// import org.springframework.stereotype.Service;
// import java.util.List;
// import java.util.stream.Collectors;
// import fr.afpa.choral_riff.repositories.UtilisateurEnsembleRepository;

// @Service
// public class InstrumentService {

//     private final InstrumentRepository instrumentRepository;
//     private final EnsembleRepository ensembleRepository;
//     private final InstrumentMapper instrumentMapper;
//     private final UtilisateurEnsembleService utilisateurEnsembleService;

//     public InstrumentService(InstrumentRepository instrumentRepository,
//             EnsembleRepository ensembleRepository,
//             InstrumentMapper instrumentMapper,  UtilisateurEnsembleService utilisateurEnsembleService) {
//         this.instrumentRepository = instrumentRepository;
//         this.ensembleRepository = ensembleRepository;
//         this.instrumentMapper = instrumentMapper;
//         this.utilisateurEnsembleService = utilisateurEnsembleService;
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
//         Instrument instrument = instrumentMapper.toEntity(dto);

//         if (ensembleId != null) {
//             Ensemble ensemble = ensembleRepository.findById(ensembleId)
//                     .orElseThrow(() -> new RuntimeException("Ensemble non trouvé avec l'ID: " + ensembleId));
//             instrument.setEnsemble(ensemble);
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
//             instrument.setEnsemble(ensemble);
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

//     // Ajouter un instrument à un ensemble avec contrôle de rôle
//     public InstrumentDto addInstrumentToEnsemble(Long ensembleId, Long utilisateurId, InstrumentDto dto) {

//         //  Vérification des droits
//         if (!utilisateurEnsembleService.utilisateurAutorise(utilisateurId, ensembleId,
//                 List.of("ADMIN", "MODERATEUR"))) {
//             throw new RuntimeException("Vous n'êtes pas autorisé à ajouter un instrument à cet ensemble");
//         }

//         Ensemble ensemble = ensembleRepository.findById(ensembleId)
//                 .orElseThrow(() -> new RuntimeException("Ensemble non trouvé avec l'ID: " + ensembleId));

//         Instrument instrument = instrumentMapper.toEntity(dto);
//         instrument.setEnsemble(ensemble);

//         return instrumentMapper.toDto(instrumentRepository.save(instrument));
//     }

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
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class InstrumentService {

    private final InstrumentRepository instrumentRepository;
    private final EnsembleRepository ensembleRepository;
    private final InstrumentMapper instrumentMapper;
    private final UtilisateurEnsembleService utilisateurEnsembleService;
    private final MorceauRepository morceauRepository; // ou DocumentInstrumentRepository

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

    // Récupérer tous les instruments
    public List<InstrumentDto> getAll() {
        return instrumentRepository.findAll().stream()
                .map(instrumentMapper::toDto)
                .collect(Collectors.toList());
    }

    // Récupérer un instrument par ID
    public InstrumentDto getById(Long id) {
        Instrument instrument = instrumentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Instrument non trouvé avec l'ID: " + id));
        return instrumentMapper.toDto(instrument);
    }

    // Créer un instrument avec validation de l’ensemble
    public InstrumentDto create(InstrumentDto dto, Long ensembleId) {
        // Vérifier si un instrument avec le même nom existe déjà
        Instrument instrument = instrumentRepository.findByNom(dto.nom())
                .orElseGet(() -> instrumentMapper.toEntity(dto));

        if (ensembleId != null) {
            Ensemble ensemble = ensembleRepository.findById(ensembleId)
                    .orElseThrow(() -> new RuntimeException("Ensemble non trouvé avec l'ID: " + ensembleId));
            Set<Ensemble> ensembles = instrument.getEnsembles();
            if (ensembles == null)
                ensembles = new HashSet<>();
            ensembles.add(ensemble);
            instrument.setEnsembles(ensembles);
        }

        Instrument saved = instrumentRepository.save(instrument);
        return instrumentMapper.toDto(saved);
    }

    // Mettre à jour un instrument existant
    public InstrumentDto update(Long id, InstrumentDto dto, Long ensembleId) {
        Instrument instrument = instrumentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Instrument non trouvé avec l'ID: " + id));

        instrumentMapper.updateEntityFromDto(dto, instrument);

        if (ensembleId != null) {
            Ensemble ensemble = ensembleRepository.findById(ensembleId)
                    .orElseThrow(() -> new RuntimeException("Ensemble non trouvé avec l'ID: " + ensembleId));

            // Many-to-Many
            Set<Ensemble> ensembles = instrument.getEnsembles();
            if (ensembles == null) {
                ensembles = new HashSet<>();
            }
            ensembles.add(ensemble);
            instrument.setEnsembles(ensembles);
        }

        Instrument updated = instrumentRepository.save(instrument);
        return instrumentMapper.toDto(updated);
    }

    // Supprimer un instrument par ID
    public void delete(Long id) {
        if (!instrumentRepository.existsById(id)) {
            throw new RuntimeException("Instrument non trouvé avec l'ID: " + id);
        }
        instrumentRepository.deleteById(id);
    }

    // Dans InstrumentService.java
    public InstrumentDto addInstrumentToMorceau(Long morceauId, Long instrumentId) {

        // 1 Récupérer le morceau
        Morceau morceau = morceauRepository.findById(morceauId)
                .orElseThrow(() -> new RuntimeException("Morceau non trouvé avec l'ID: " + morceauId));

        // 2 Récupérer l'instrument
        Instrument instrument = instrumentRepository.findById(instrumentId)
                .orElseThrow(() -> new RuntimeException("Instrument non trouvé avec l'ID: " + instrumentId));

        // 4 Sauvegarder le morceau
        morceauRepository.save(morceau);

        // 5 Retourner le DTO de l'instrument (ou du morceau selon ton besoin)
        return instrumentMapper.toDto(instrument);
    }

}
