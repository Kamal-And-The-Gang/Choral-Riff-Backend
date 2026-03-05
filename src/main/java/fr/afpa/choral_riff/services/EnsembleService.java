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
     * Crée un nouvel ensemble musical à partir d'un DTO et assigne le créateur.
     *
     * <p>
     * Cette méthode réalise les étapes suivantes :
     * <ol>
     * <li>Convertit le {@link EnsembleDto} en entité {@link Ensemble} via
     * {@link EnsembleMapper}.</li>
     * <li>Récupère l'utilisateur créateur à partir de son identifiant
     * {@code userId}.</li>
     * <li>Crée une relation {@link UtilisateurEnsemble} liant le créateur à
     * l'ensemble
     * avec le rôle ADMIN et marque l'utilisateur comme créateur.</li>
     * <li>Pour les types restreints d'ensemble (QUATUOR ou BAND), le créateur est
     * systématiquement ADMIN.</li>
     * <li>Ajoute la relation créateur à l'ensemble.</li>
     * <li>Enregistre l'ensemble en base de données avec la relation utilisateur
     * (cascade automatique).</li>
     * <li>Retourne un {@link EnsembleDto} représentant l'ensemble créé, incluant le
     * rôle de l'utilisateur créateur.</li>
     * </ol>
     * </p>
     *
     * @param dto    le DTO contenant les informations de l'ensemble à créer
     * @param userId l'identifiant de l'utilisateur créateur
     * @return le {@link EnsembleDto} correspondant à l'ensemble créé
     * @throws RuntimeException si l'identifiant du créateur n'est pas trouvé dans
     *                          la base de données
     */
    public EnsembleDto create(EnsembleDto dto, Long userId) {
        Ensemble ensemble = ensembleMapper.toEntity(dto);

        Optional<Utilisateur> createur = utilisateurRepository.findById(userId);

        if (createur.isPresent()) {

            Set<UtilisateurEnsemble> userEnsemble = ensemble.getUtilisateurEnsembles();

            Role rolePourCreateur = Role.ADMIN;

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

    @Transactional
    public void delete(Long ensembleId, Long userId) {
        if (!hasRights(userId, ensembleId)) {
            throw new RuntimeException("Vous n'avez pas les droits pour supprimer cet ensemble");
        }

        Ensemble ensemble = ensembleRepository.findByIdWithRelations(ensembleId)
                .orElseThrow(() -> new RuntimeException("Ensemble non trouvé"));

        // Marquer notifications liées à l'ensemble comme invalides
        notificationRepository.markAsInvalidByEnsembleId(ensembleId);

        // Supprimer l'ensemble (Hibernate s'occupe de cascades pour morceaux →
        // documents)
        ensembleRepository.delete(ensemble);
    }

    /**
     * Récupère tous les ensembles musicaux avec leurs relations principales et les
     * convertit en DTO pour l'affichage.
     *
     * <p>
     * Cette méthode utilise une requête avec <code>JOIN FETCH</code> pour charger
     * simultanément les utilisateurs associés et les morceaux de chaque ensemble,
     * afin d'éviter les problèmes de LazyInitializationException.
     * </p>
     *
     * <p>
     * Pour chaque ensemble :
     * <ul>
     * <li>Les informations de base (id, nom, description, type, date de création)
     * sont copiées dans un {@link EnsembleDto}.</li>
     * <li>Le créateur (utilisateur avec le rôle ADMIN) est identifié et ses
     * informations (id, nom, prénom) sont ajoutées.</li>
     * <li>Le nombre total de membres dans l'ensemble est calculé.</li>
     * <li>Le rôle spécifique de l'utilisateur courant et le flag de créateur
     * sont laissés à <code>null</code> / <code>false</code> car cette
     * méthode n'est pas filtrée par utilisateur.</li>
     * </ul>
     * </p>
     *
     * @return une liste de {@link EnsembleDto} représentant tous les ensembles
     *         existants
     *         avec leurs relations chargées (utilisateurs et morceaux)
     */

    public List<EnsembleDto> getAll() {
        return ensembleRepository.findAllWithRelations().stream() // <- fetch join
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
