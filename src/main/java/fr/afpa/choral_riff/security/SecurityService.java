package fr.afpa.choral_riff.security;

import fr.afpa.choral_riff.entity.Utilisateur;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

  private final UtilisateurRepository utilisateurRepository;

  public SecurityService(UtilisateurRepository utilisateurRepository) {
    this.utilisateurRepository = utilisateurRepository;
  }

  // 18/12
  /**
   * Récupère l'ID de l'utilisateur actuellement authentifié.
   * @return
   */
  public Long getCurrentUserId() {
    Authentication auth =
      SecurityContextHolder.getContext().getAuthentication();

    if (
      auth == null ||
      !auth.isAuthenticated() ||
      "anonymousUser".equals(auth.getPrincipal())
    ) {
      throw new RuntimeException("Utilisateur non connecté");
    }
    // c’est uniquement pour les tests ou cas fictifs (@WithMockUser, etc.).
    // Pour la production, tu dois gérer le vrai principal, qui sera typiquement :

    // Ton entité Utilisateur si tu as un UserDetailsService
    // qui retourne directement ton objet Utilisateur.

    // org.springframework.security.core.userdetails.User
    // (ou une classe custom) si tu utilises la classe Spring User pour
    // l’authentification.
    Object principal = auth.getPrincipal();

    // Cas test avec @WithMockUser
    if (principal instanceof String) {
      return 1L; // ID fictif pour tests
    }

    // Si tu utilises ton propre objet Utilisateur comme principal
    if (principal instanceof Utilisateur) {
      return ((Utilisateur) principal).getId();
    }

    // Si Spring Security a renvoyé un UserDetails standard
    if (principal instanceof UserDetails) {
      String email = ((UserDetails) principal).getUsername();
      Utilisateur user = utilisateurRepository
        .findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
      return user.getId();
    }

    throw new RuntimeException(
      "Type de principal non géré : " + principal.getClass().getName()
    );
  }
}
