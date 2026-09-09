package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.entity.Ensemble;
import fr.afpa.choral_riff.entity.Role;
import fr.afpa.choral_riff.entity.Utilisateur;
import fr.afpa.choral_riff.entity.UtilisateurEnsemble;
import fr.afpa.choral_riff.repositories.EnsembleRepository;
import fr.afpa.choral_riff.repositories.UtilisateurEnsembleRepository;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import fr.afpa.choral_riff.dto.UtilisateurEnsembleDTO;

/**
 * Service métier permettant de gérer les relations entre un {@link Utilisateur}
 * et un {@link Ensemble}.
 *
 * <p>
 * Cette classe centralise la logique liée :
 * <ul>
 *     <li>à l’adhésion d’un utilisateur à un ensemble,</li>
 *     <li>à la vérification d’appartenance,</li>
 *     <li>à la gestion des rôles d’un utilisateur au sein d’un ensemble.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Elle s’appuie sur les repositories :
 * <ul>
 *     <li>{@link UtilisateurEnsembleRepository}</li>
 *     <li>{@link UtilisateurRepository}</li>
 *     <li>{@link EnsembleRepository}</li>
 * </ul>
 * </p>
 *
 * <p>
 * Les opérations d’écriture sont exécutées dans un contexte transactionnel.
 * </p>
 */


@Service
public class UtilisateurEnsembleService {

    private final UtilisateurEnsembleRepository utilisateurEnsembleRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final EnsembleRepository ensembleRepository;

    public UtilisateurEnsembleService(
            UtilisateurEnsembleRepository utilisateurEnsembleRepository,
            UtilisateurRepository utilisateurRepository,
            EnsembleRepository ensembleRepository) {
        this.utilisateurEnsembleRepository = utilisateurEnsembleRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.ensembleRepository = ensembleRepository;
    }

    // Vérifie si un utilisateur est déjà membre d'un ensemble
    public boolean estMembreDeLensemble(Long utilisateurId, Long ensembleId) {
        return utilisateurEnsembleRepository.existsByUtilisateurIdAndEnsembleId(utilisateurId, ensembleId);
    }

    /**
     * Vérifie si un utilisateur a un rôle autorisé dans un ensemble.
     * 
     * @param utilisateurId id de l'utilisateur
     * @param ensembleId    id de l'ensemble
     * @param roles         liste des rôles autorisés, ex: ["OWNER", "MODERATEUR"]
     * @return true si l'utilisateur a l'un de ces rôles, false sinon
     */
    public boolean utilisateurAutorise(Long utilisateurId, Long ensembleId, List<String> roles) {
        // Convertit les String en enum Role
        List<Role> rolesEnum = roles.stream()
                .map(Role::valueOf) // attention : valueOf lance IllegalArgumentException si le String ne correspond
                                    // pas
                .toList();

        return utilisateurEnsembleRepository.existsByUtilisateur_IdAndEnsemble_IdAndRoleDansEnsembleIn(
                utilisateurId, ensembleId, rolesEnum);
    }

    // Nouvelle méthode pour récupérer le rôle d'un utilisateur dans un ensemble
    public Role getRoleUtilisateurDansEnsemble(Long utilisateurId, Long ensembleId) {
        return utilisateurEnsembleRepository.findByUtilisateur_IdAndEnsemble_Id(utilisateurId, ensembleId)
                .orElseThrow(() -> new RuntimeException("Utilisateur pas trouvé dans l'ensemble"))
                .getRoleDansEnsemble();
    }

    // @Transactional
    // public void rattacherUtilisateurAEnsemble(Long utilisateurId, Long
    // ensembleId) {
    // // Vérifie si l'utilisateur existe
    // Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
    // .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

    // // Vérifie si l'ensemble existe
    // Ensemble ensemble = ensembleRepository.findById(ensembleId)
    // .orElseThrow(() -> new RuntimeException("Ensemble introuvable"));

    // // Vérifie si l'utilisateur est déjà membre
    // boolean dejaMembre = utilisateurEnsembleRepository
    // .existsByUtilisateurIdAndEnsembleId(utilisateurId, ensembleId);

    // if (!dejaMembre) {
    // UtilisateurEnsemble ue = new UtilisateurEnsemble();
    // ue.setUtilisateur(utilisateur);
    // ue.setEnsemble(ensemble);
    // ue.setRoleDansEnsemble(Role.MEMBRE);
    // ue.setDateAdhesion(LocalDateTime.now());
    // // Ajouter le nom complet
    // ue.setNomComplet(utilisateur.getPrenom() + " " + utilisateur.getNom());
    // utilisateurEnsembleRepository.saveAndFlush(ue);
    // } else {
    // throw new RuntimeException("L'utilisateur est déjà membre de cet ensemble");
    // }
    // }

    // public boolean estMembreDeLensemble(Long utilisateurId, Long ensembleId) {
    // return
    // utilisateurEnsembleRepository.existsByUtilisateurIdAndEnsembleId(utilisateurId,
    // ensembleId);
    // }

    @Transactional
    public void rattacherUtilisateurAEnsemble(Long utilisateurId, Long ensembleId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Ensemble ensemble = ensembleRepository.findById(ensembleId)
                .orElseThrow(() -> new RuntimeException("Ensemble introuvable"));

        boolean dejaMembre = utilisateurEnsembleRepository
                .existsByUtilisateurIdAndEnsembleId(utilisateurId, ensembleId);

        if (!dejaMembre) {
            UtilisateurEnsemble ue = new UtilisateurEnsemble();
            ue.setUtilisateur(utilisateur);
            ue.setEnsemble(ensemble);
            ue.setRoleDansEnsemble(Role.MEMBRE);
            ue.setDateAdhesion(LocalDateTime.now());
            ue.setNomComplet(utilisateur.getPrenom() + " " + utilisateur.getNom());
            utilisateurEnsembleRepository.saveAndFlush(ue);
        }
        // Sinon ne rien faire, juste continuer
    }


        @Transactional
    public void changerRole(
            Long acteurId,
            Long cibleId,
            Long ensembleId,
            Role nouveauRole) {

        // Vérifier que l'acteur est OWNER dans cet ensemble
        Role roleActeur = getRoleUtilisateurDansEnsemble(acteurId, ensembleId);

        if (roleActeur != Role.OWNER) {
            throw new SecurityException(
                    "Seul le OWNER peut modifier les rôles dans cet ensemble");
        }

        // Récupérer la personne dont on veut modifier le rôle
        UtilisateurEnsemble utilisateurEnsemble =
                utilisateurEnsembleRepository
                        .findByUtilisateur_IdAndEnsemble_Id(cibleId, ensembleId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Utilisateur non trouvé dans cet ensemble"));

        // L'OWNER ne peut modifier que MEMBRE <-> MODERATEUR
        Role ancienRole = utilisateurEnsemble.getRoleDansEnsemble();

        if (ancienRole == Role.OWNER) {
            throw new SecurityException(
                    "Le rôle OWNER ne peut pas être modifié");
        }

        if (nouveauRole == Role.OWNER) {
            throw new SecurityException(
                    "Impossible d'attribuer le rôle OWNER");
        }

        if (nouveauRole != Role.MEMBRE && nouveauRole != Role.MODERATEUR) {
            throw new IllegalArgumentException(
                    "Le nouveau rôle doit être MEMBRE ou MODERATEUR");
        }

        utilisateurEnsemble.setRoleDansEnsemble(nouveauRole);

        utilisateurEnsembleRepository.save(utilisateurEnsemble);
    }

    public boolean peutModifierRole(
            Long acteurId,
            Long cibleId,
            Long ensembleId,
            Role nouveauRole) {

        Role roleActeur = getRoleUtilisateurDansEnsemble(acteurId, ensembleId);
        Role roleCible = getRoleUtilisateurDansEnsemble(cibleId, ensembleId);

        if (roleActeur == Role.MEMBRE) {
            return false;
        }

        if (roleActeur == Role.OWNER) {
            return (roleCible == Role.MEMBRE || roleCible == Role.MODERATEUR)
                    && (nouveauRole == Role.MEMBRE || nouveauRole == Role.MODERATEUR);
        }

        if (roleActeur == Role.MODERATEUR) {
            return roleCible == Role.MEMBRE
                    && nouveauRole == Role.MODERATEUR;
        }

        return false;
    }

    public List<UtilisateurEnsembleDTO> getMembresDeLEnsemble(Long ensembleId) {

        return utilisateurEnsembleRepository.findByEnsembleId(ensembleId)
                .stream()
                .map(ue -> new UtilisateurEnsembleDTO(
                        ue.getUtilisateur().getId(),
                        ue.getUtilisateur().getNom(),
                        ue.getUtilisateur().getPrenom(),
                        ue.getRoleDansEnsemble().name(),
                        ue.isCreator()
                ))
                .toList();
    }

    @Transactional
    public void retirerUtilisateurDeLEnsemble(
            Long acteurId,
            Long cibleId,
            Long ensembleId) {

        // L'acteur doit être OWNER
        Role roleActeur =
                getRoleUtilisateurDansEnsemble(acteurId, ensembleId);

        if (roleActeur != Role.OWNER) {
            throw new SecurityException(
                    "Seul le OWNER peut retirer un membre de cet ensemble");
        }

        // Récupérer la relation de la personne à retirer
        UtilisateurEnsemble membre =
                utilisateurEnsembleRepository
                        .findByUtilisateur_IdAndEnsemble_Id(
                                cibleId,
                                ensembleId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Utilisateur non trouvé dans cet ensemble"));

        // Impossible de retirer le OWNER
        if (membre.getRoleDansEnsemble() == Role.OWNER) {
            throw new SecurityException(
                    "Impossible de retirer le OWNER de l'ensemble");
        }

        // Suppression uniquement de la relation
        utilisateurEnsembleRepository
                .deleteByUtilisateur_IdAndEnsemble_Id(
                        cibleId,
                        ensembleId);
    }

}

