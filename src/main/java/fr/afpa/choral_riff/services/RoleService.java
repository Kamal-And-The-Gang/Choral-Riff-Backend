package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.entity.Role;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    // Exemple de méthode pour assigner un rôle
    public void assignRole(Long userId, Long ensembleId, Role role) {
        // Ici on ajoute la logique pour enregistrer le rôle en base
        System.out.println("Assignation du rôle " + role + " à l'utilisateur " + userId + " pour l'ensemble " + ensembleId);
    }
}
