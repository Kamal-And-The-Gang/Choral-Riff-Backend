package fr.afpa.choral_riff.dto;

import java.util.List;
import java.util.Objects;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UtilisateurDTO {
    private Long id_utilisateur;
    private String nom;
    private String prenom;
    private LocalDate date_inscription;
    private String email;
    // pas de motDePasse ici pour la réponse

    // getters/setters...
}
