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
import fr.afpa.choral_riff.entity.Role;

import java.util.Objects;

@Service
public class MorceauService {
    private final EnsembleService ensembleService;
    private final UtilisateurEnsembleService utilisateurEnsembleService;

    private final MorceauRepository morceauRepository;
    private final EnsembleRepository ensembleRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final MorceauMapper morceauMapper;
    private final NotificationService notificationService;

    public MorceauService(
            MorceauRepository morceauRepository,
            EnsembleRepository ensembleRepository,
            UtilisateurRepository utilisateurRepository,
            MorceauMapper morceauMapper,
            EnsembleService ensembleService,
            NotificationService notificationService,
            UtilisateurEnsembleService utilisateurEnsembleService // <-- ici
    ) {
        this.morceauRepository = morceauRepository;
        this.ensembleRepository = ensembleRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.morceauMapper = morceauMapper;
        this.notificationService = notificationService;
        this.ensembleService = ensembleService;
        this.utilisateurEnsembleService = utilisateurEnsembleService; // <-- initialisation
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

    public MorceauDto create(MorceauDto dto, Long userId) {
        Objects.requireNonNull(dto, "Le DTO ne doit pas être null");

        // --- Vérification : l'ensemble existe ---
        if (dto.ensembleId() == null) {
            throw new RuntimeException("Un morceau doit être rattaché à un ensemble");
        }

        Ensemble ensemble = ensembleRepository.findById(dto.ensembleId())
                .orElseThrow(() -> new RuntimeException("Ensemble non trouvé avec l'ID: " + dto.ensembleId()));

        // --- Vérifie que l'utilisateur est membre de l'ensemble ---
        if (!utilisateurEnsembleService.utilisateurAutorise(userId, dto.ensembleId(),
                List.of("ADMIN", "MODERATEUR", "MEMBRE"))) {
            throw new RuntimeException("Vous devez être membre de l'ensemble pour ajouter un morceau");
        }

        // --- Transformer le DTO en entité ---
        Morceau morceau = Objects.requireNonNull(
                morceauMapper.toEntity(dto),
                "Le mapper a retourné null");

        // --- Lier l'ensemble ---
        morceau.setEnsemble(ensemble);

        // --- Récupérer le créateur depuis l'utilisateur authentifié ---
        Utilisateur createur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        morceau.setCreateur(createur);

        // --- Sauvegarder le morceau ---
        Morceau saved = morceauRepository.save(morceau);

        // --- Notification (optionnel selon ton besoin) ---
        notificationService.notifyMorceauAjoute(saved);

        // --- Retourner le DTO pour le front ---
        return morceauMapper.toDto(saved);
    }

    // Mettre à jour un morceau existant
    public MorceauDto update(Long id, MorceauDto dto) {
        Objects.requireNonNull(dto, "Le DTO ne doit pas être null");
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
    // public void delete(Long id) {
    // if (!morceauRepository.existsById(id)) {
    // throw new RuntimeException("Morceau non trouvé avec l'ID: " + id);
    // }
    // morceauRepository.deleteById(id);
    // }

    public void delete(Long morceauId, Long userId) {
        // Récupérer le morceau
        Morceau morceau = morceauRepository.findById(morceauId)
                .orElseThrow(() -> new RuntimeException("Morceau non trouvé avec l'ID : " + morceauId));

        Long ensembleId = morceau.getEnsemble().getId();

        // Vérifier que l'utilisateur est membre de l'ensemble
        if (!utilisateurEnsembleService.utilisateurAutorise(userId, ensembleId,
                List.of("ADMIN", "MODERATEUR", "MEMBRE"))) {
            throw new RuntimeException("Vous devez être membre de l'ensemble pour supprimer ce morceau");
        }

        // Vérifier les droits : créateur ou rôle élevé
        Role role = utilisateurEnsembleService.getRoleUtilisateurDansEnsemble(userId, ensembleId);
        boolean isAdminOrModerator = role == Role.ADMIN || role == Role.MODERATEUR;
        boolean isCreateur = morceau.getCreateur().getId().equals(userId);

        if (!isCreateur && !isAdminOrModerator) {
            throw new RuntimeException("Vous n'avez pas les droits pour supprimer ce morceau");
        }

        // Suppression autorisée
        morceauRepository.delete(morceau);
    }

    // public MorceauDto findLastAddedMorceauByEnsemble(Long ensembleId) {
    // return morceauRepository.findTopByEnsembleIdOrderByIdDesc(ensembleId) // <--
    // Appel au Repository
    // .map(morceauMapper::toDto)
    // .orElseThrow(() -> new RuntimeException("Aucun morceau trouvé pour l'ensemble
    // " + ensembleId));
    // }

    public MorceauDto findLastAddedMorceauByEnsemble(Long ensembleId) {
        return morceauRepository.findTopByEnsembleIdOrderByIdDesc(ensembleId)
                .map(morceauMapper::toDto)
                .orElse(null);
    }

}
