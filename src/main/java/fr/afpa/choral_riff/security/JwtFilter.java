
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
protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain) throws ServletException, IOException {

    String requestPath = request.getServletPath();

    // Laisse passer les routes publiques
    if (requestPath.startsWith("/api/auth")
            || requestPath.startsWith("/api/utilisateur")
            || requestPath.equals("/api/documents/upload")) {
        chain.doFilter(request, response);
        return;
    }

    // 1. Essaie de récupérer le JWT depuis l'en-tête Authorization
    String jwt = null;
    String authHeader = request.getHeader("Authorization");

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
        jwt = authHeader.substring(7);
    }

    // 2. Si aucun JWT dans l'en-tête, cherche le cookie accessToken
    if (jwt == null && request.getCookies() != null) {
        for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
            if ("accessToken".equals(cookie.getName())) {
                jwt = cookie.getValue();
                break;
            }
        }
    }

    // 3. Valide le JWT et authentifie l'utilisateur
    if (jwt != null
            && SecurityContextHolder.getContext().getAuthentication() == null) {
        try {
            String username = jwtService.extractUsername(jwt);

            if (username != null) {
                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities());

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request));

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }
    }

    // Une seule exécution du filtre suivant
    chain.doFilter(request, response);
}


}

