package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.entity.Utilisateur;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;
import java.util.Collections;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UtilisateurRepository utilisateurRepository;

  public UserDetailsServiceImpl(UtilisateurRepository utilisateurRepository) {
    this.utilisateurRepository = utilisateurRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String email)
    throws UsernameNotFoundException {
    Utilisateur utilisateur = utilisateurRepository
      .findByEmail(email)
      .orElseThrow(() ->
        new UsernameNotFoundException("Utilisateur non trouvé : " + email)
      );

    // Retourne un UserDetails minimal avec un rôle
    return org.springframework.security.core.userdetails.User.builder()
      .username(utilisateur.getEmail())
      .password(utilisateur.getPassword()) // mot de passe BCrypt
      .authorities("ROLE_USER") // <- important
      .build();
  }
}
