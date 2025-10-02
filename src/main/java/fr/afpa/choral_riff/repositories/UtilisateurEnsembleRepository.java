package fr.afpa.choral_riff.repositories;

import fr.afpa.choral_riff.entity.UtilisateurEnsemble;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtilisateurEnsembleRepository extends JpaRepository<UtilisateurEnsemble, Long> {
}
