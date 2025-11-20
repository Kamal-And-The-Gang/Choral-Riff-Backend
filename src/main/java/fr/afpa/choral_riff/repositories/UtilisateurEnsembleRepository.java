package fr.afpa.choral_riff.repositories;

import fr.afpa.choral_riff.entity.Role;
import fr.afpa.choral_riff.entity.UtilisateurEnsemble;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtilisateurEnsembleRepository extends JpaRepository<UtilisateurEnsemble, Long> {

    boolean existsByUtilisateur_IdAndEnsemble_IdAndRoleDansEnsembleIn(Long utilisateurId,
                                                                     Long ensembleId,
                                                                     List<Role> roles);

    boolean existsByUtilisateurIdAndEnsembleId(Long id, Long id2);

    Optional<UtilisateurEnsemble> findByUtilisateur_IdAndEnsemble_Id(Long utilisateurId, Long ensembleId);
}

