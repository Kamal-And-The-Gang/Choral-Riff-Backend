package fr.afpa.choral_riff.repositories;

import fr.afpa.choral_riff.entity.Instrument;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstrumentRepository extends JpaRepository<Instrument, Long> {

  // Méthode pour récupérer un instrument par son nom
  Optional<Instrument> findByNom(String nom);
}
