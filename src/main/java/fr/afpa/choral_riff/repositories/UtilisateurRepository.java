package fr.afpa.choral_riff.repositories;

import fr.afpa.choral_riff.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);

    @Query("SELECT u FROM Utilisateur u LEFT JOIN FETCH u.utilisateurEnsembles ue LEFT JOIN FETCH ue.ensemble WHERE u.email = :email")
    Optional<Utilisateur> findByEmailWithEnsembles(@Param("email") String email);
}

