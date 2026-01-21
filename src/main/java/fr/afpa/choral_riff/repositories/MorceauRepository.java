package fr.afpa.choral_riff.repositories;

import fr.afpa.choral_riff.entity.Morceau;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MorceauRepository extends JpaRepository<Morceau, Long> {

    List<Morceau> findByEnsembleId(Long ensembleId);

    Optional<Morceau> findTopByEnsembleIdOrderByIdDesc(Long ensembleId);
    // --- AJOUTÉ pour la suppression en cascade ---
    @Modifying
    @Transactional
    @Query("DELETE FROM Morceau m WHERE m.ensemble.id = :ensembleId")
    void deleteByEnsembleId(@Param("ensembleId") Long ensembleId);



}
