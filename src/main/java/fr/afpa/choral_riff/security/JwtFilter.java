package fr.afpa.choral_riff.security;

import fr.afpa.choral_riff.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtFilter(JwtService jwtService, UserDetailsServiceImpl userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Exclure les endpoints publics (exemple : `/auth/**` et `/api/users/**`)
        String requestPath = request.getServletPath();
        if (requestPath.startsWith("/api/auth") || requestPath.startsWith("/api/utilisateurs")) {
            chain.doFilter(request, response);
            return;
        }

        // Récupère l'en-tête Authorization
        String authHeader = request.getHeader("Authorization");

        // Ajout de logs pour debug
        System.out.println("=== [DEBUG JWT FILTER] ===");
        System.out.println("Request Path: " + requestPath);
        System.out.println("Authorization Header: " + authHeader);

        String jwt = null;
        String username = null;

        // Vérifie si le header est présent et commence par "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // Extrait le token après "Bearer "
            System.out.println("Extracted JWT: " + jwt);

            username = jwtService.extractUsername(jwt);// Extrait le username du token
            System.out.println("Extracted Username: " + username);
        }

        // Vérifie si le username n'est pas déjà authentifié dans le contexte de
        // sécurité
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Charge les informations de l'utilisateur
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Valide le token
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Crée un objet d'authentification valide
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Définit le contexte de sécurité pour cette requête
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("User authenticated: " + username);
            }
        } else {
            System.out.println("Token invalid for user: " + username);
        }

        // Passe au filtre suivant
        chain.doFilter(request, response);
    }

}
