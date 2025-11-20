package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.entity.Role;
import fr.afpa.choral_riff.repositories.UtilisateurEnsembleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtilisateurEnsembleService {

    private final UtilisateurEnsembleRepository utilisateurEnsembleRepository;

    public UtilisateurEnsembleService(UtilisateurEnsembleRepository utilisateurEnsembleRepository) {
        this.utilisateurEnsembleRepository = utilisateurEnsembleRepository;
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

}
