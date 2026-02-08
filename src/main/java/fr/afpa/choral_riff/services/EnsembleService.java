package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.dto.EnsembleDto;

import fr.afpa.choral_riff.entity.Ensemble;
import fr.afpa.choral_riff.entity.Role;
import fr.afpa.choral_riff.entity.TypeEnsemble;
import fr.afpa.choral_riff.entity.Utilisateur;
import fr.afpa.choral_riff.entity.UtilisateurEnsemble;
import fr.afpa.choral_riff.mapper.EnsembleMapper;
import fr.afpa.choral_riff.repositories.EnsembleRepository;
import fr.afpa.choral_riff.repositories.InvitationRepository;
import fr.afpa.choral_riff.repositories.MorceauRepository;
import fr.afpa.choral_riff.repositories.NotificationRepository;
import fr.afpa.choral_riff.repositories.UtilisateurEnsembleRepository;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service métier pour gérer les opérations liées aux ensembles musicaux.
 * 
 * Ce service permet de :
 * 
 * Créer un nouvel ensemble
 * Récupérer tous les ensembles existants
 * Récupérer un ensemble par son identifiant
 * Mettre à jour un ensemble existant
 * Supprimer un ensemble
 * 
 *
 * Utilise :
 * 
 * EnsembleRepository : accès à la base de données
 * EnsembleMapper : conversion entre Entity et DTO
 * 
 *
 * 
 */
@Service
public class EnsembleService {

    private final EnsembleRepository ensembleRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurEnsembleRepository utilisateurEnsembleRepository; // <-- ajout
    private final EnsembleMapper ensembleMapper;
    private final NotificationRepository notificationRepository;
    private final InvitationRepository invitationRepository;
    private final MorceauRepository morceauRepository;

    // private final RoleService roleService;

    /**
     * Constructeur avec injection des dépendances.
     *
     * @param ensembleRepository le repository pour l'entité Ensemble
     * @param ensembleMapper     le mapper pour convertir entre Entity et DTO
     */
    public EnsembleService(
            EnsembleRepository ensembleRepository,
            EnsembleMapper ensembleMapper,
            UtilisateurRepository utilisateurRepository,
            UtilisateurEnsembleRepository utilisateurEnsembleRepository,
            NotificationRepository notificationRepository,
            InvitationRepository invitationRepository,
            MorceauRepository morceauRepository) {

        this.ensembleRepository = ensembleRepository;
        this.ensembleMapper = ensembleMapper;
        this.utilisateurRepository = utilisateurRepository;
        this.utilisateurEnsembleRepository = utilisateurEnsembleRepository;
        this.notificationRepository = notificationRepository;
        this.invitationRepository = invitationRepository;
        this.morceauRepository = morceauRepository;
    }

    /**
     * Récupère tous les ensembles enregistrés.
     *
     * @return une liste de DTO représentant les ensembles
     */
    public List<EnsembleDto> getAll(Long userId) {
        return ensembleRepository.findAll().stream()
                .map(e -> ensembleMapper.toDto(e, userId))
                .toList();
    }

    /**
     * Crée un nouvel ensemble à partir d’un DTO.
     *
     * @param dto le DTO contenant les données du nouvel ensemble
     * @return le DTO de l’ensemble créé
     */
    public EnsembleDto create(EnsembleDto dto, Long userId) {
        Ensemble ensemble = ensembleMapper.toEntity(dto);

        // récupération du créateur à la partir de la base de données
        Optional<Utilisateur> createur = utilisateurRepository.findById(userId);
        // si on a bien retrouvé le créateur
        if (createur.isPresent()) {
            // mise à jour de la liste des utilisateurs
            // d'abord on récupére la liste
            Set<UtilisateurEnsemble> userEnsemble = ensemble.getUtilisateurEnsembles();

            // <<< Ici on remplace la création de l'objet UtilisateurEnsemble
            // rôle par défaut du créateur
            Role rolePourCreateur = Role.ADMIN;

            // si c'est un groupe restreint (quatuor ou groupe de rock), tous les membres
            // sont admins
            if (dto.getTypeEnsemble() == TypeEnsemble.QUATUOR || dto.getTypeEnsemble() == TypeEnsemble.BAND) {
                rolePourCreateur = Role.ADMIN; // reste ADMIN, logique pour montrer qu'on force admin
            }

            // on la modifie avec un nouvel objet de la classe USerEnsemble
            UtilisateurEnsemble utilisateurEnsemble = new UtilisateurEnsemble(
                    createur.get(),
                    ensemble,
                    Role.ADMIN,
                    LocalDateTime.now());
            // On indique que c'est le créateur de l'ensemble
            utilisateurEnsemble.setCreator(true);
            // On ajoute à la liste des utilisateurs de l'ensemble

            userEnsemble.add(utilisateurEnsemble);

            // on enregistre l'ensemble

            Ensemble saved = ensembleRepository.save(ensemble);
            return ensembleMapper.toDto(saved, userId);
        }

        // créateur non retrouvé, notamelement c'est la panique
        throw new RuntimeException("Création ensemble - id créateur non retrouvé");
    }

    // 15/11/2025
    public List<EnsembleDto> getAllForUser(Long userId) {
        Utilisateur user = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));

        return user.getUtilisateurEnsembles().stream()
                .map(utilisateurEnsemble -> {
                    Ensemble ensemble = utilisateurEnsemble.getEnsemble();
                    EnsembleDto dto = ensembleMapper.toDto(ensemble, userId);

                    // Ajout du rôle pour l'utilisateur actuel
                    dto.setUserRole(utilisateurEnsemble.getRoleDansEnsemble().name()); // ADMIN, MEMBRE, etc.

                    return dto;
                })
                .toList();
    }

    /**
     * Récupère un ensemble par son identifiant.
     *
     * @param id l'identifiant de l’ensemble
     * @return le DTO correspondant
     * @throws EntityNotFoundException si aucun ensemble n’a été trouvé
     */

    // public EnsembleDto getById(Long id, Long userId) {
    // Ensemble ensemble = ensembleRepository.findById(id)
    // .orElseThrow(() -> new EntityNotFoundException("Ensemble non trouvé avec l’ID
    // : " + id));
    // return ensembleMapper.toDto(ensemble, userId);
    // }
    public EnsembleDto getById(Long id, Long userId) {
        Ensemble ensemble = ensembleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ensemble non trouvé avec l’ID : " + id));

        EnsembleDto dto = ensembleMapper.toDto(ensemble, userId);

        // Récupérer la relation utilisateur-ensemble pour remplir les rôles
        if (userId != null) {
            utilisateurEnsembleRepository.findByUtilisateur_IdAndEnsemble_Id(userId, id)
                    .ifPresent(ue -> {
                        dto.setUserRole(ue.getRoleDansEnsemble().name()); // ADMIN, MODERATEUR, MEMBRE
                        dto.setIsCreator(ue.isCreator()); // true si créateur
                    });
        }

        return dto;
    }

    public EnsembleDto getByIdForUser(Long ensembleId, Long userId) {
        Ensemble ensemble = ensembleRepository.findById(ensembleId)
                .orElseThrow(() -> new EntityNotFoundException("Ensemble non trouvé avec l’ID : " + ensembleId));

        EnsembleDto dto = ensembleMapper.toDto(ensemble, userId);

        // Récupérer la relation utilisateur-ensemble
        utilisateurEnsembleRepository.findByUtilisateur_IdAndEnsemble_Id(userId, ensembleId)
                .ifPresent(ue -> {
                    dto.setUserRole(ue.getRoleDansEnsemble().name()); // MODERATEUR, MEMBRE...
                    dto.setIsCreator(ue.isCreator()); // boolean pour le créateur
                });

        return dto;
    }

    /**
     * Met à jour les informations d’un ensemble existant.
     *
     * @param id  l'identifiant de l’ensemble à mettre à jour
     * @param dto le DTO contenant les nouvelles données
     * @return le DTO de l’ensemble mis à jour
     * @throws EntityNotFoundException si aucun ensemble avec cet ID n’existe
     */

    public EnsembleDto update(Long id, EnsembleDto dto, Long userId) {
        Ensemble existing = ensembleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Impossible de mettre à jour : ID " + id + " introuvable"));

        if (!hasRights(userId, id)) {
            throw new RuntimeException("Vous n'avez pas les droits pour modifier cet ensemble");
        }

        existing.setNom(dto.getNom());
        existing.setDescription(dto.getDescription());
        existing.setTypeEnsemble(dto.getTypeEnsemble());

        Ensemble updated = ensembleRepository.save(existing);
        return ensembleMapper.toDto(updated, userId);
    }

    /**
     * Vérifie si un utilisateur a les droits de MODÉRATEUR ou ADMIN sur un ensemble
     */
    public boolean hasRights(Long userId, Long ensembleId) {
        return utilisateurEnsembleRepository
                .findByUtilisateur_IdAndEnsemble_Id(userId, ensembleId)
                .map(ue -> ue.isCreator() || ue.getRoleDansEnsemble() == Role.ADMIN)

                .orElse(false);
    }

    // @Transactional
    // public void delete(Long ensembleId, Long userId) {
    // if (!hasRights(userId, ensembleId)) {
    // throw new RuntimeException("Vous n'avez pas les droits pour supprimer cet
    // ensemble");
    // }

    // Ensemble ensemble = ensembleRepository.findById(ensembleId)
    // .orElseThrow(() -> new EntityNotFoundException(
    // "Impossible de supprimer : ensemble avec ID " + ensembleId + "
    // introuvable"));

    // // Vider explicitement les collections (optionnel)
    // ensemble.getInvitations().clear();
    // ensemble.getMorceaux().clear();
    // ensemble.getUtilisateurEnsembles().clear();

    // ensembleRepository.delete(ensemble);
    // }

    // @Transactional
    // public void delete(Long ensembleId, Long userId) {
    // if (!hasRights(userId, ensembleId)) {
    // throw new RuntimeException("Vous n'avez pas les droits pour supprimer cet
    // ensemble");
    // }

    // // Supprime les relations utilisateur-ensemble
    // utilisateurEnsembleRepository.deleteByEnsemble_Id(ensembleId);

    // // Supprime les morceaux
    // morceauRepository.deleteByEnsembleId(ensembleId);

    // // Supprime les invitations
    // invitationRepository.deleteByEnsembleId(ensembleId);

    // // Supprime l'ensemble lui-même
    // ensembleRepository.deleteById(ensembleId);
    // }

    // @Transactional
    // public void delete(Long ensembleId, Long userId) {
    //     if (!hasRights(userId, ensembleId)) {
    //         throw new RuntimeException("Vous n'avez pas les droits pour supprimer cet ensemble");
    //     }

    //     Ensemble ensemble = ensembleRepository.findById(ensembleId)
    //             .orElseThrow(() -> new RuntimeException("Ensemble non trouvé"));

    //     // Hibernate supprime tout ce qui est lié grâce aux cascades
    //     ensembleRepository.delete(ensemble);
    // }


@Transactional
public void delete(Long ensembleId, Long userId) {
    if (!hasRights(userId, ensembleId)) {
        throw new RuntimeException("Vous n'avez pas les droits pour supprimer cet ensemble");
    }

    Ensemble ensemble = ensembleRepository.findById(ensembleId)
            .orElseThrow(() -> new RuntimeException("Ensemble non trouvé"));

    // ✅ Marquer notifications liées à l'ensemble comme invalides
    notificationRepository.markAsInvalidByEnsembleId(ensembleId);

    // Supprimer relations utilisateur-ensemble
    utilisateurEnsembleRepository.deleteByEnsemble_Id(ensembleId);

    // Supprimer les morceaux
    morceauRepository.deleteByEnsembleId(ensembleId);

    // Supprimer les invitations
    invitationRepository.deleteByEnsembleId(ensembleId);

    // Supprimer l'ensemble lui-même
    ensembleRepository.delete(ensemble);
}






    

    /**
     * Récupère tous les ensembles enregistrés, sans information spécifique à un
     * utilisateur.
     */
    public List<EnsembleDto> getAll() {
        return ensembleRepository.findAll().stream()
                .map(ensemble -> {
                    EnsembleDto dto = new EnsembleDto();
                    dto.setId(ensemble.getId());
                    dto.setNom(ensemble.getNom());
                    dto.setDescription(ensemble.getDescription());
                    dto.setTypeEnsemble(ensemble.getTypeEnsemble());
                    dto.setDateCreation(ensemble.getDateCreation());

                    // Créateur (ADMIN)
                    ensemble.getUtilisateurEnsembles().stream()
                            .filter(ue -> ue.getRoleDansEnsemble() == Role.ADMIN)
                            .findFirst()
                            .ifPresent(ue -> {
                                dto.setCreatedBy(ue.getUtilisateur().getId());
                                dto.setCreateurNom(ue.getUtilisateur().getNom());
                                dto.setCreateurPrenom(ue.getUtilisateur().getPrenom());
                            });

                    dto.setNombreMembres(ensemble.getUtilisateurEnsembles().size());

                    // Pas de rôle spécifique à l'utilisateur
                    dto.setUserRole(null);
                    dto.setIsCreator(false);

                    return dto;
                })
                .toList();
    }

    public int getNombreMembres(Long ensembleId) {
        return (int) utilisateurEnsembleRepository.countByEnsemble_Id(ensembleId);
    }

    /**
     * Vérifie si un utilisateur fait partie d’un ensemble (quel que soit son rôle)
     */
    public boolean isMember(Long userId, Long ensembleId) {
        return utilisateurEnsembleRepository
                .existsByUtilisateurIdAndEnsembleId(userId, ensembleId);
    }

    public EnsembleDto getById(Long id) {
        Ensemble ensemble = ensembleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ensemble non trouvé"));
        return ensembleMapper.toDto(ensemble);
    }

}
