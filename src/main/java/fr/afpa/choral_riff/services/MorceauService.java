package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.dto.MorceauDto;
import fr.afpa.choral_riff.entity.Ensemble;
import fr.afpa.choral_riff.entity.Morceau;
import fr.afpa.choral_riff.entity.Utilisateur;
import fr.afpa.choral_riff.mapper.MorceauMapper;
import fr.afpa.choral_riff.repositories.EnsembleRepository;
import fr.afpa.choral_riff.repositories.MorceauRepository;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MorceauService {

    private final MorceauRepository morceauRepository;
    private final EnsembleRepository ensembleRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final MorceauMapper morceauMapper;

    public MorceauService(MorceauRepository morceauRepository,
            EnsembleRepository ensembleRepository,
            UtilisateurRepository utilisateurRepository,
            MorceauMapper morceauMapper) {
        this.morceauRepository = morceauRepository;
        this.ensembleRepository = ensembleRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.morceauMapper = morceauMapper;
    }

    // Récupérer tous les morceaux
    public List<MorceauDto> getAll() {
        return morceauRepository.findAll().stream()
                .map(morceauMapper::toDto)
                .collect(Collectors.toList());
    }

    // Récupérer et filtrer les ensembles
    public List<MorceauDto> getAllByEnsembleId(Long ensembleId) {
        return morceauRepository.findByEnsembleId(ensembleId)
                .stream()
                .map(morceauMapper::toDto)
                .toList();
    }

    // Récupérer un morceau par son ID
    public MorceauDto getById(Long id) {
        Morceau morceau = morceauRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Morceau non trouvé avec l'ID: " + id));
        return morceauMapper.toDto(morceau);
    }

    // Créer un morceau
    public MorceauDto create(MorceauDto dto) {
        Morceau morceau = morceauMapper.toEntity(dto);

        if (dto.ensembleId() != null) {
            Ensemble ensemble = ensembleRepository.findById(dto.ensembleId())
                    .orElseThrow(() -> new RuntimeException("Ensemble non trouvé avec l'ID: " + dto.ensembleId()));
            morceau.setEnsemble(ensemble);
        }

        if (dto.createurId() != null) {
            Utilisateur createur = utilisateurRepository.findById(dto.createurId())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + dto.createurId()));
            morceau.setCreateur(createur);
        }

        Morceau saved = morceauRepository.save(morceau);
        return morceauMapper.toDto(saved);
    }

    // Mettre à jour un morceau existant
    public MorceauDto update(Long id, MorceauDto dto) {
        Morceau morceau = morceauRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Morceau non trouvé avec l'ID: " + id));

        // Mettre à jour les champs simples
        // Morceau = entité plus complexe que instrument, update manuel et sécurisé.
        morceau.setTitre(dto.titre());
        morceau.setCompositeur(dto.compositeur());
        morceau.setGenre(dto.genre());
        morceau.setDescriptif(dto.descriptif());

        // Mettre à jour les relations
        if (dto.ensembleId() != null) {
            Ensemble ensemble = ensembleRepository.findById(dto.ensembleId())
                    .orElseThrow(() -> new RuntimeException("Ensemble non trouvé avec l'ID: " + dto.ensembleId()));
            morceau.setEnsemble(ensemble);
        }

        if (dto.createurId() != null) {
            Utilisateur createur = utilisateurRepository.findById(dto.createurId())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + dto.createurId()));
            morceau.setCreateur(createur);
        }

        Morceau updated = morceauRepository.save(morceau);
        return morceauMapper.toDto(updated);
    }

    // Supprimer un morceau par son ID
    public void delete(Long id) {
        if (!morceauRepository.existsById(id)) {
            throw new RuntimeException("Morceau non trouvé avec l'ID: " + id);
        }
        morceauRepository.deleteById(id);
    }
}
