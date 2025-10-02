package fr.afpa.choral_riff.repositories;

import fr.afpa.choral_riff.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    List<Invitation> findByEnsembleId(Long ensembleId);
    Optional<Invitation> findByToken(String token);
}
