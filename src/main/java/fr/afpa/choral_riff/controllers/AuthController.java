package fr.afpa.choral_riff.controllers;

import fr.afpa.choral_riff.dto.LoginDTO;
import fr.afpa.choral_riff.dto.RegisterDto;
import fr.afpa.choral_riff.dto.UtilisateurDto;
import fr.afpa.choral_riff.services.LoginService;
import fr.afpa.choral_riff.services.UtilisateurService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Contrôleur REST pour l'authentification (login).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final LoginService loginService;
    private final UtilisateurService utilisateurService;

    public AuthController(LoginService loginService, UtilisateurService utilisateurService) {
        this.loginService = loginService;
        this.utilisateurService = utilisateurService;
    }

    /**
     * Endpoint pour authentifier un utilisateur et générer les tokens JWT.
     *
     * @param loginDTO email et mot de passe envoyés par le client
     * @return un objet JSON contenant accessToken et refreshToken
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDTO loginDTO) {
        Map<String, String> tokens = loginService.login(loginDTO);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/register")
    public ResponseEntity<UtilisateurDto> register(@RequestBody RegisterDto dto) {
        UtilisateurDto created = utilisateurService.register(dto);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        Map<String, String> result = loginService.logout(null);
        return ResponseEntity.ok(result);
    }

}
