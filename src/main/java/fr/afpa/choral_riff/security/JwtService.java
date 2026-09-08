
// //         Map<String, Object> claims = new HashMap<>();
// //         return createToken(claims, user.getEmail(), jwtExpiration);
// //     }

// //     // Génération d'un refresh token
// //     public String generateRefreshToken(Utilisateur user, String refreshTokenId) {
// //         Map<String, Object> claims = new HashMap<>();
// //         claims.put("refreshTokenId", refreshTokenId);
// //         return createToken(claims, user.getEmail(), refreshTokenExpiration);
// //     }

// //     // Génération d'un ID unique pour le refresh token
// //     public String generateRefreshTokenId() {
// //         return UUID.randomUUID().toString();
// //     }

// //     // Création du JWT
// //     private String createToken(Map<String, Object> claims, String subject, long expiration) {
// //         return Jwts.builder()
// //                 .setClaims(claims)
// //                 .setSubject(subject)
// //                 .setIssuedAt(new Date())
// //                 .setExpiration(new Date(System.currentTimeMillis() + expiration))
// //                 .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
// //                 .compact();
// //     }

// //     // Validation du token
// //     public boolean isTokenValid(String token, Utilisateur user) {
// //         final String username = extractUsername(token);
// //         return (username.equals(user.getEmail()) && !isTokenExpired(token));
// //     }

// //     // private boolean isTokenExpired(String token) {
// //     //     Claims claims = Jwts.parserBuilder()
// //     //             .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
// //     //             .build()
// //     //             .parseClaimsJws(token)
// //     //             .getBody();
// //     //     return claims.getExpiration().before(new Date());
// //     // }
// //     private boolean isTokenExpired(String token) {
// //     Claims claims = Jwts.parserBuilder()
// //             .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
// //             .build()
// //             .parseClaimsJws(token)
// //             .getBody();
// //     return claims.getExpiration().before(new Date());
// // }

// //     // Surcharge pour UserDetails (Spring Security)
// //    public boolean isTokenValid(String token, org.springframework.security.core.userdetails.UserDetails userDetails) {
// //     final String username = extractUsername(token);
// //     return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
// // }

// // }
// package fr.afpa.choral_riff.security;

// import fr.afpa.choral_riff.entity.Utilisateur;
// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.security.Keys;
// import org.springframework.stereotype.Service;

// import javax.crypto.SecretKey;
// import java.nio.charset.StandardCharsets;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.UUID;

// @Service
// public class JwtService {

//     //  AU MOINS 32 caractères
//     private static final String SECRET =
//             "ta_cle_secrete_super_longue_tres_securisee_32_chars_min";

//     private static final long JWT_EXPIRATION = 1000 * 60 * 60; // 1h
//     private static final long REFRESH_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7j

//     private final SecretKey secretKey =
//             Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

//     // ===================== EXTRACTION =====================

//     public String extractUsername(String token) {
//         return extractAllClaims(token).getSubject();
//     }

//     private Claims extractAllClaims(String token) {
//         return Jwts.parser()
//                 .verifyWith(secretKey)
//                 .build()
//                 .parseSignedClaims(token)
//                 .getPayload();
//     }

//     // ===================== GÉNÉRATION =====================

//     public String generateToken(Utilisateur user) {
//         return createToken(new HashMap<>(), user.getEmail(), JWT_EXPIRATION);
//     }

//     public String generateRefreshToken(Utilisateur user, String refreshTokenId) {
//         Map<String, Object> claims = new HashMap<>();
//         claims.put("refreshTokenId", refreshTokenId);
//         return createToken(claims, user.getEmail(), REFRESH_EXPIRATION);
//     }

//     public String generateRefreshTokenId() {
//         return UUID.randomUUID().toString();
//     }

//     private String createToken(Map<String, Object> claims,
//                                String subject,
//                                long expiration) {

//         return Jwts.builder()
//                 .claims(claims)
//                 .subject(subject)
//                 .issuedAt(new Date())
//                 .expiration(new Date(System.currentTimeMillis() + expiration))
//                 .signWith(secretKey) // ✅ plus de SignatureAlgorithm
//                 .compact();
//     }

//     // ===================== VALIDATION =====================

//     public boolean isTokenValid(String token, Utilisateur user) {
//         return extractUsername(token).equals(user.getEmail())
//                 && !isTokenExpired(token);
//     }

//     public boolean isTokenValid(
//             String token,
//             org.springframework.security.core.userdetails.UserDetails userDetails) {

//         return extractUsername(token).equals(userDetails.getUsername())
//                 && !isTokenExpired(token);
//     }

//     private boolean isTokenExpired(String token) {
//         return extractAllClaims(token)
//                 .getExpiration()
//                 .before(new Date());
//     }
// }
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
