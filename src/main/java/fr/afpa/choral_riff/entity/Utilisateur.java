package fr.afpa.choral_riff.entity;

import jakarta.persistence.*;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Entité représentant un utilisateur de l'application Choral Riff.
 *
 * Stocke les informations relatives aux
 * utilisateurs de la plateforme : identité, connexion, et associations aux
 * morceaux, documents et ensembles.
 *
 * Elle implémente l'interface {@link UserDetails} afin de s'intégrer avec
 * Spring Security pour la gestion de l'authentification et des autorisations.
 */

@Entity
@Table(name = "utilisateur")
public class Utilisateur implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String motDePasse;

    private String photoProfil;

    // Associations
    @OneToMany(mappedBy = "createur")
    private Set<Morceau> morceauxCree;
    /**
     * Ensemble des documents ajoutés par l'utilisateur.
     */
    @OneToMany(mappedBy = "utilisateurAjout")
    private Set<Document> documentsAjoutes;
    /**
     * Ensembles musicaux auxquels l'utilisateur appartient.
     */
    @ManyToMany(mappedBy = "membres")
    private Set<Ensemble> ensemble;

    /**
     * Rôles liés de l'utilisateur dans des ensembles.
     */
    @OneToMany(mappedBy = "utilisateur")
    private Set<UtilisateurEnsemble> utilisateurEnsembles;

    // -------------------- UserDetails --------------------

    // @Override
    // public Collection<? extends GrantedAuthority> getAuthorities() {
    // return List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
    // }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return utilisateurEnsembles.stream()
                .map(ue -> new SimpleGrantedAuthority("ROLE_" + ue.getRoleDansEnsemble().name().toUpperCase()))
                .collect(Collectors.toSet());
    }
    // @Override
    // public Collection<? extends GrantedAuthority> getAuthorities() {
    // throw new UnsupportedOperationException("Unimplemented method
    // 'getAuthorities'");
    // }

    @Override
    public String getPassword() {
        return motDePasse;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getPhotoProfil() {
        return photoProfil;
    }

    public void setPhotoProfil(String photoProfil) {
        this.photoProfil = photoProfil;
    }

    public Set<Morceau> getMorceauxCree() {
        return morceauxCree;
    }

    public void setMorceauxCree(Set<Morceau> morceauxCree) {
        this.morceauxCree = morceauxCree;
    }

    public Set<Document> getDocumentsAjoutes() {
        return documentsAjoutes;
    }

    public void setDocumentsAjoutes(Set<Document> documentsAjoutes) {
        this.documentsAjoutes = documentsAjoutes;
    }

    public Set<Ensemble> getEnsembles() {
        return ensemble;
    }

    public void setEnsembles(Set<Ensemble> ensembles) {
        this.ensemble = ensembles;
    }

    public Set<UtilisateurEnsemble> getUtilisateurEnsembles() {
        return utilisateurEnsembles;
    }

    public void setUtilisateurEnsembles(Set<UtilisateurEnsemble> utilisateurEnsembles) {
        this.utilisateurEnsembles = utilisateurEnsembles;
    }

}