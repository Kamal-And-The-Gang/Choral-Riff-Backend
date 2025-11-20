package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.dto.EnsembleDto;

import fr.afpa.choral_riff.entity.Ensemble;
import fr.afpa.choral_riff.entity.Role;
import fr.afpa.choral_riff.entity.Utilisateur;
import fr.afpa.choral_riff.entity.UtilisateurEnsemble;
import fr.afpa.choral_riff.mapper.EnsembleMapper;
import fr.afpa.choral_riff.repositories.EnsembleRepository;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    private final EnsembleMapper ensembleMapper;
    private final RoleService roleService;

    /**
     * Constructeur avec injection des dépendances.
     *
     * @param ensembleRepository le repository pour l'entité Ensemble
     * @param ensembleMapper     le mapper pour convertir entre Entity et DTO
     */
    public EnsembleService(EnsembleRepository ensembleRepository,
            EnsembleMapper ensembleMapper,
            RoleService roleService,
            UtilisateurRepository utilisateurRepository) {
        this.ensembleRepository = ensembleRepository;
        this.ensembleMapper = ensembleMapper;
        this.roleService = roleService;
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Récupère tous les ensembles enregistrés.
     *
     * @return une liste de DTO représentant les ensembles
     */
    public List<EnsembleDto> getAll() {
        return ensembleRepository.findAll().stream()
                .map(ensembleMapper::toDto)
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
            // on la modifie avec un nouvel objet de la classe USerEnsemble
            UtilisateurEnsemble utilisateurEnsemble = new UtilisateurEnsemble(createur.get(),
                    ensemble,
                    Role.ADMIN,
                    LocalDate.now());

            userEnsemble.add(utilisateurEnsemble);
            // ensemble.setDateCreation(userId);
            // enregistre le créateur

            Ensemble saved = ensembleRepository.save(ensemble);
            return ensembleMapper.toDto(saved);
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
                EnsembleDto dto = ensembleMapper.toDto(ensemble);

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
    public EnsembleDto getById(Long id) {
        Ensemble ensemble = ensembleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ensemble non trouvé avec l’ID : " + id));
        return ensembleMapper.toDto(ensemble);
    }

    /**
     * Met à jour les informations d’un ensemble existant.
     *
     * @param id  l'identifiant de l’ensemble à mettre à jour
     * @param dto le DTO contenant les nouvelles données
     * @return le DTO de l’ensemble mis à jour
     * @throws EntityNotFoundException si aucun ensemble avec cet ID n’existe
     */
    //
    public EnsembleDto update(Long id, EnsembleDto dto) {
        Ensemble existing = ensembleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Impossible de mettre à jour : ID " + id + " introuvable"));

        // Mettre à jour uniquement les champs sauf l'ID
        existing.setNom(dto.getNom());
        existing.setDescription(dto.getDescription());

        // ... autres champs sauf id

        Ensemble updated = ensembleRepository.save(existing);
        return ensembleMapper.toDto(updated);
    }

    /**
     * Supprime un ensemble existant par son identifiant.
     *
     * @param id l’identifiant de l’ensemble à supprimer
     * @throws EntityNotFoundException si aucun ensemble avec cet ID n’existe
     */
    public void delete(Long id) {
        if (!ensembleRepository.existsById(id)) {
            throw new EntityNotFoundException("Impossible de supprimer : ensemble avec ID " + id + " introuvable");
        }
        ensembleRepository.deleteById(id);
    }

}
