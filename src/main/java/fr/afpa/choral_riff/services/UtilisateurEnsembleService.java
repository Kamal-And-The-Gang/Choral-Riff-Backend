package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.entity.Ensemble;
import fr.afpa.choral_riff.entity.Role;
import fr.afpa.choral_riff.entity.Utilisateur;
import fr.afpa.choral_riff.entity.Utilisateur.UtilisateurDTO;
import fr.afpa.choral_riff.entity.UtilisateurEnsemble;
import fr.afpa.choral_riff.repositories.EnsembleRepository;
import fr.afpa.choral_riff.repositories.UtilisateurEnsembleRepository;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    /**
     * Vérifie si un utilisateur a un rôle autorisé dans un ensemble.
     * 
     * @param utilisateurId id de l'utilisateur
     * @param ensembleId    id de l'ensemble
     * @param roles         liste des rôles autorisés, ex: ["ADMIN", "MODERATEUR"]
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

    @Transactional
    public void rattacherUtilisateurAEnsemble(Long utilisateurId, Long ensembleId) {
        // Vérifie si l'utilisateur existe
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Vérifie si l'ensemble existe
        Ensemble ensemble = ensembleRepository.findById(ensembleId)
                .orElseThrow(() -> new RuntimeException("Ensemble introuvable"));

        // Vérifie si l'utilisateur est déjà membre
        boolean dejaMembre = utilisateurEnsembleRepository
                .existsByUtilisateurIdAndEnsembleId(utilisateurId, ensembleId);

        if (!dejaMembre) {
            UtilisateurEnsemble ue = new UtilisateurEnsemble();
            ue.setUtilisateur(utilisateur);
            ue.setEnsemble(ensemble);
            ue.setRoleDansEnsemble(Role.MEMBRE);
            ue.setDateAdhesion(LocalDateTime.now());
            // Ajouter le nom complet
            ue.setNomComplet(utilisateur.getPrenom() + " " + utilisateur.getNom());
            utilisateurEnsembleRepository.saveAndFlush(ue);
        } else {
            throw new RuntimeException("L'utilisateur est déjà membre de cet ensemble");
        }
    }

    // public List<UtilisateurDTO> getUtilisateursAvecNomComplet(Long ensembleId) {
    //     return utilisateurEnsembleRepository.findUtilisateursAvecNomComplet(ensembleId);
    // }

}
