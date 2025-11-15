package fr.afpa.choral_riff.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import fr.afpa.choral_riff.entity.Utilisateur;
import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    private final String secretKey;
    private final long jwtExpiration;
    private final long jwtRefreshExpiration;

    public JwtService(

            @Value("${security.jwt.secret-key}") String secretKey,
            @Value("${security.jwt.expiration-time-in-seconds}") long jwtExpiration,
            @Value("${security.jwt.refresh-expiration-time-in-seconds:604800}") long jwtRefreshExpiration) {

        this.secretKey = secretKey;
        this.jwtExpiration = jwtExpiration;
        this.jwtRefreshExpiration = jwtRefreshExpiration; // Par défaut 7 jours si non spécifié
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Génère un JWT à partir d'un objet de la classe "Utilisateur"
     * 
     * Le JWT contient les informations suivantes :
     * - id
     * - prenom
     * - nom
     * - email
     * 
     * @param utilisateur
     * @return
     */
    public String generateToken(Utilisateur utilisateur) {
        Map<String, Object> extraClaims = new HashMap<>();

        // Ajout des infos dans le token
        extraClaims.put("id", utilisateur.getId());
        extraClaims.put("prenom", utilisateur.getPrenom());
        extraClaims.put("nom", utilisateur.getNom());
        extraClaims.put("email", utilisateur.getEmail());

        // Génération du JWT
        return buildToken(extraClaims, utilisateur, jwtExpiration);
    }

    /**
     * Génère un refresh token avec un ID unique
     *
     * @param userDetails détails de l'utilisateur
     * @param tokenId     identifiant unique du token
     * @return refresh token
     */
    public String generateRefreshToken(UserDetails userDetails, String tokenId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("tokenId", tokenId);
        return buildToken(claims, userDetails, jwtRefreshExpiration);
    }

    /**
     * Génère un identifiant unique pour le refresh token
     *
     * @return UUID sous forme de chaîne
     */
    public String generateRefreshTokenId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Extrait l'ID du token depuis un refresh token
     *
     * @param token refresh token
     * @return ID du token
     */
    public String extractTokenId(String token) {
        return extractClaim(token, claims -> claims.get("tokenId", String.class));
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }

    public long getRefreshExpirationTime() {
        return jwtRefreshExpiration;
    }

    public Duration getAccessTokenDuration() {
        return Duration.ofSeconds(jwtExpiration / 1000);
    }

    public Duration getRefreshTokenDuration() {
        return Duration.ofSeconds(jwtRefreshExpiration / 1000);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(getSignInKey())
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && isTokenNotExpired(token);
    }

    /**
     * Vérifie si un token est valide sans vérifier l'utilisateur
     *
     * @param token le token à valider
     * @return vrai si le token est valide et non expiré
     */
    public boolean isTokenValid(String token) {
        try {
            return isTokenNotExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenNotExpired(String token) {
        return !extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
