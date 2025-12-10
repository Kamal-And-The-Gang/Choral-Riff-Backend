package fr.afpa.choral_riff.repositories;

import fr.afpa.choral_riff.entity.Role;
import fr.afpa.choral_riff.entity.Utilisateur.UtilisateurDTO;
import fr.afpa.choral_riff.entity.UtilisateurEnsemble;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UtilisateurEnsembleRepository extends JpaRepository<UtilisateurEnsemble, Long> {

    boolean existsByUtilisateur_IdAndEnsemble_IdAndRoleDansEnsembleIn(Long utilisateurId,
            Long ensembleId,
            List<Role> roles);

    boolean existsByUtilisateurIdAndEnsembleId(Long id, Long id2);

    List<UtilisateurEnsemble> findByEnsembleId(Long ensembleId);

    Optional<UtilisateurEnsemble> findByUtilisateur_IdAndEnsemble_Id(Long utilisateurId, Long ensembleId);

//     @Query("SELECT new fr.afpa.choral_riff.dto.UtilisateurDTO(" +
//             "ue.utilisateur.id, CONCAT(ue.utilisateur.prenom, ' ', ue.utilisateur.nom)) " +
//             "FROM UtilisateurEnsemble ue " +
//             "WHERE ue.ensemble.id = :ensembleId")
//     List<UtilisateurDTO> findUtilisateursAvecNomComplet(@Param("ensembleId") Long ensembleId);
}
