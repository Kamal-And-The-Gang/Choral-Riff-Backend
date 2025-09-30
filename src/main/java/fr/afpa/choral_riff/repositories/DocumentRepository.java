package fr.afpa.choral_riff.repositories;

import fr.afpa.choral_riff.entity.Document;
import fr.afpa.choral_riff.entity.Morceau;
import fr.afpa.choral_riff.entity.Utilisateur;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface de gestion des accès à la table "Document"
 * Hérite de JpaRepository pour bénéficier des méthodes CRUD de base.
 */

public interface DocumentRepository extends JpaRepository<Document, Long> {

  /**
   * Récupère la liste des documents ajoutés par un utilisateur donné.
   *
   * @param utilisateur L'utilisateur dont on veut connaître les documents.
   * @return Une liste de documents ajoutés par cet utilisateur.
   */
  List<Document> findByUtilisateur(Utilisateur utilisateur);

  /**
   * Récupère tous les documents associés à un morceau donné.
   *
   * @param morceau Le morceau pour lequel on veut les documents.
   * @return Une liste de documents liés à ce morceau.
   */
  List<Document> findByMorceau(Morceau morceau);
}
