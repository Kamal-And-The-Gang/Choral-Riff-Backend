package fr.afpa.choral_riff.repositories;

import fr.afpa.choral_riff.entity.Notification;
import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  @Query("""
          SELECT n
          FROM Notification n
          LEFT JOIN FETCH n.invitation i
          LEFT JOIN FETCH i.utilisateur
          WHERE n.utilisateur.id = :utilisateurId
          AND n.valid = true
          ORDER BY n.dateCreation DESC
      """)
  List<Notification> findValidByUtilisateurIdWithInvitation(
      @Param("utilisateurId") Long utilisateurId);

  @Modifying
  @Transactional
  @Query("UPDATE Notification n SET n.valid = false WHERE n.ensembleId = :ensembleId")
  void markAsInvalidByEnsembleId(@Param("ensembleId") Long ensembleId);

  void deleteAllByInvitationId(Long invitationId);

  @Query("""
          SELECT n FROM Notification n
          WHERE n.utilisateur.id = :utilisateurId
          AND n.valid = true
          ORDER BY n.dateCreation DESC
      """)
  List<Notification> findValidByUtilisateurId(@Param("utilisateurId") Long utilisateurId);

}
