package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.security.JwtService;
import fr.afpa.choral_riff.dto.LoginDTO;
import fr.afpa.choral_riff.entity.Utilisateur;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UtilisateurRepository utilisateurRepository,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authentifie un utilisateur et génère un access + refresh token.
     *
     * @param loginDTO login/password envoyés par le client
     * @return map contenant les tokens
     */
    public Map<String, String> login(LoginDTO loginDTO) {
        // Authentification via Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));

        Utilisateur user = (Utilisateur) authentication.getPrincipal();

        // Génération du refresh token avec un ID unique
        String refreshTokenId = jwtService.generateRefreshTokenId();
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user, refreshTokenId);

        // Retourner les 2 tokens dans un JSON-like map
        Map<String, String> response = new HashMap<>();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);

        return response;
    }

    public Map<String, String> logout(String refreshTokenId) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Déconnexion réussie");
        return response;
    }

}
