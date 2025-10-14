package fr.afpa.choral_riff.repositories;

import fr.afpa.choral_riff.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    List<Invitation> findByEnsembleId(Long ensembleId);
    Optional<Invitation> findByToken(String token);


    boolean existsByEmailInvite(String emailInvite);

    boolean existsByEmailInviteAndEnsembleId(String emailInvite, Long ensembleId);
}
