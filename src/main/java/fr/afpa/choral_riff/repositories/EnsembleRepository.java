package fr.afpa.choral_riff.repositories;

import fr.afpa.choral_riff.entity.Ensemble;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnsembleRepository extends JpaRepository<Ensemble, Long> {
}
