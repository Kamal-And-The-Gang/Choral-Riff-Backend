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
      """)
  List<Notification> findByUtilisateurIdWithInvitation(@Param("utilisateurId") Long utilisateurId);

  // Supprimer toutes les notifications liées à un ensemble
  @Modifying
  @Transactional
  @Query("DELETE FROM Notification n WHERE n.ensembleId = :ensembleId")
  void deleteAllByEnsembleId(@Param("ensembleId") Long ensembleId);

  void deleteAllByInvitationId(Long invitationId);

  @Query("SELECT n FROM Notification n WHERE n.utilisateur.id = :utilisateurId")
  List<Notification> findByUtilisateurId(@Param("utilisateurId") Long utilisateurId);

}
