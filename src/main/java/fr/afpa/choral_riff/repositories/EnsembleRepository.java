package fr.afpa.choral_riff.repositories;

import fr.afpa.choral_riff.entity.Ensemble;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EnsembleRepository extends JpaRepository<Ensemble, Long> {

    // void deleteByNom(String nom);

    @Query("SELECT DISTINCT e FROM Ensemble e " +
       "LEFT JOIN FETCH e.utilisateurEnsembles ue " +
       "LEFT JOIN FETCH e.morceaux " +
       "WHERE e.nom = :nom")
Optional<Ensemble> findByNomWithRelations(@Param("nom") String nom);
     // Récupérer tous les ensembles avec utilisateurs et morceaux
    @Query("SELECT DISTINCT e FROM Ensemble e " +
           "LEFT JOIN FETCH e.utilisateurEnsembles ue " +
           "LEFT JOIN FETCH e.morceaux")
    List<Ensemble> findAllWithRelations();

    // Récupérer un ensemble spécifique avec utilisateurs et morceaux
    @Query("SELECT DISTINCT e FROM Ensemble e " +
           "LEFT JOIN FETCH e.utilisateurEnsembles ue " +
           "LEFT JOIN FETCH e.morceaux " +
           "WHERE e.id = :id")
    Optional<Ensemble> findByIdWithRelations(@Param("id") Long id);

}
