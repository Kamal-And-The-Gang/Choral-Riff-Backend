package fr.afpa.choral_riff.repositories;

import fr.afpa.choral_riff.entity.Invitation;
import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    List<Invitation> findByEnsembleId(Long ensembleId);

    Optional<Invitation> findByToken(String token);

    boolean existsByEmailInvite(String emailInvite);

    boolean existsByEmailInviteAndEnsembleId(String emailInvite, Long ensembleId);

    Optional<Invitation> findByEmailInviteAndEnsembleId(String emailInvite, Long ensembleId);

    // --- AJOUTÉ pour la suppression en cascade ---
    @Modifying
    @Transactional
    @Query("DELETE FROM Invitation i WHERE i.ensemble.id = :ensembleId")
    void deleteByEnsembleId(@Param("ensembleId") Long ensembleId);
}
