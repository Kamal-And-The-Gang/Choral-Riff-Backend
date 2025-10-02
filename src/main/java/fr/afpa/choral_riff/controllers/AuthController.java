package fr.afpa.choral_riff.controllers;

import fr.afpa.choral_riff.dto.LoginDTO;
import fr.afpa.choral_riff.services.LoginService;
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

    public AuthController(LoginService loginService) {
        this.loginService = loginService;
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
}

