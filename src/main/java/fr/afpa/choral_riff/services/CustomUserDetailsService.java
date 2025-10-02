package fr.afpa.choral_riff.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import fr.afpa.choral_riff.entity.Utilisateur;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    public CustomUserDetailsService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Recherche l'utilisateur par email
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec email : " + email));

        // Récupérer les rôles liés à chaque ensemble
      Set<GrantedAuthority> authorities = utilisateur.getUtilisateurEnsembles().stream()
    .map(ue -> new SimpleGrantedAuthority("ROLE_" + ue.getRoleDansEnsemble().name()))
    .collect(Collectors.toSet());

        // Construire et retourner un UserDetails Spring Security
        return new org.springframework.security.core.userdetails.User(
                utilisateur.getEmail(),
                utilisateur.getMotDePasse(),
                authorities);
    }
}

