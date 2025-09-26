package fr.afpa.choral_riff.repositories;

import fr.afpa.choral_riff.entity.DocumentInstrument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentInstrumentRepository extends JpaRepository<DocumentInstrument, Long> {
}
