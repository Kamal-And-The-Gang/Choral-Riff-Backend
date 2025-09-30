package fr.afpa.choral_riff.repositories;

import fr.afpa.choral_riff.entity.DocumentInstrument;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository pour gérer les opérations CRUD sur l'entité
 * {@link DocumentInstrument}.
 * 
 * Fournit des méthodes pour accéder aux relations entre Documents et
 * Instruments.
 */

public interface DocumentInstrumentRepository extends JpaRepository<DocumentInstrument, Long> {
}
