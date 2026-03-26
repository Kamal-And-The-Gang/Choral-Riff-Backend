package fr.afpa.choral_riff.repositories;

import fr.afpa.choral_riff.entity.Utilisateur;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UtilisateurRepositoryTest {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Test
    void shouldSaveAndFindUtilisateur() {
        // Création d'un utilisateur
        Utilisateur user = new Utilisateur();
        user.setNom("Dupont");
        user.setPrenom("Jean");
        user.setEmail("jean@test.com");
        user.setMotDePasse("password");

        // Sauvegarde en base
        Utilisateur saved = utilisateurRepository.save(user);

        // Vérification qu'un ID a été généré
        assertNotNull(saved.getId());

        // Recherche par email
        Optional<Utilisateur> found = utilisateurRepository.findByEmail("jean@test.com");
        assertTrue(found.isPresent());
        assertEquals("Jean", found.get().getPrenom());
    }

    @Test
    void shouldDeleteByEmail() {
        // Création et sauvegarde
        Utilisateur user = new Utilisateur();
        user.setNom("Martin");
        user.setPrenom("Paul");
        user.setEmail("paul@test.com");
        user.setMotDePasse("password");
        utilisateurRepository.save(user);

        // Suppression par email
        utilisateurRepository.deleteByEmail("paul@test.com");

        // Vérification que l'utilisateur n'existe plus
        Optional<Utilisateur> found = utilisateurRepository.findByEmail("paul@test.com");
        assertTrue(found.isEmpty());
    }
}