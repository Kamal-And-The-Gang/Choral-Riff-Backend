package fr.afpa.choral_riff.repositories;

import fr.afpa.choral_riff.entity.Morceau;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MorceauRepository extends JpaRepository<Morceau, Long> {

    List<Morceau> findByEnsembleId(Long ensembleId);
    // Optional<Morceau> findTopByOrderByMorceauIdDesc(); 

}
