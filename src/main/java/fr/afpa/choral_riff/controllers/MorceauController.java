package fr.afpa.choral_riff.controllers;

import fr.afpa.choral_riff.dto.MorceauDto;
import fr.afpa.choral_riff.entity.Utilisateur;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;
import fr.afpa.choral_riff.services.MorceauService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST pour gérer les morceaux.
 */
@RestController
@RequestMapping("/api/morceaux")
public class MorceauController {

  private final MorceauService morceauService;
  private final UtilisateurRepository utilisateurRepository;

  public MorceauController(
    MorceauService morceauService,
    UtilisateurRepository utilisateurRepository
  ) {
    this.morceauService = morceauService;
    this.utilisateurRepository = utilisateurRepository;
  }

  /**
   * Récupérer tous les morceaux
   */
  @GetMapping
  public ResponseEntity<List<MorceauDto>> getAll() {
    return ResponseEntity.ok(morceauService.getAll());
  }

  /**
   * Récupérer tous les morceaux d’un ensemble spécifique
   */
  @GetMapping("/ensemble/{ensembleId}")
  public ResponseEntity<List<MorceauDto>> getAllByEnsembleId(
    @PathVariable Long ensembleId
  ) {
    return ResponseEntity.ok(morceauService.getAllByEnsembleId(ensembleId));
  }

  /**
   * Récupérer un morceau par son ID
   */
  @GetMapping("/{id}")
  public ResponseEntity<MorceauDto> getById(@PathVariable Long id) {
    return ResponseEntity.ok(morceauService.getById(id));
  }

  /**
   * Créer un nouveau morceau
   * @param dto
   * @param userId
   * @return
   */

  @PostMapping
  public ResponseEntity<MorceauDto> create(
    @RequestBody MorceauDto dto,
    @RequestParam Long userId
  ) {
    // Appel au service en passant le userId
    MorceauDto created = morceauService.create(dto, userId);
    return ResponseEntity.ok(created);
  }

  /**
   * Récupérer le dernier morceau ajouté
   */
  @GetMapping("/ensemble/{ensembleId}/last")
  public ResponseEntity<MorceauDto> getLastMorceauByEnsemble(
    @PathVariable Long ensembleId
  ) {
    // 1. Appel à la méthode du service qui filtre par ensembleId
    MorceauDto lastMorceau = morceauService.findLastAddedMorceauByEnsemble(
      ensembleId
    );

    if (lastMorceau != null) {
      return ResponseEntity.ok(lastMorceau);
    } else {
      // 2. Retourne 404 Not Found si aucun morceau n'est trouvé pour cet ensemble
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Mettre à jour un morceau existant
   */
  @PutMapping("/{id}")
  public ResponseEntity<MorceauDto> update(
    @PathVariable Long id,
    @RequestBody MorceauDto dto
  ) {
    return ResponseEntity.ok(morceauService.update(id, dto));
  }

  /**
   *
   * @param id
   * @param authentication
   * @return
   */

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
    @PathVariable Long id,
    Authentication authentication
  ) {
    System.out.println(">>> Entrée dans MorceauController.delete()");

    if (authentication == null || !authentication.isAuthenticated()) {
      return ResponseEntity.status(401).build();
    }

    Utilisateur utilisateur = utilisateurRepository
      .findByEmail(authentication.getName())
      .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

    morceauService.delete(id, utilisateur.getId());

    return ResponseEntity.noContent().build();
  }
}
