package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.dto.RegisterDto;
import fr.afpa.choral_riff.dto.UtilisateurDto;
import fr.afpa.choral_riff.entity.Utilisateur;
import fr.afpa.choral_riff.entity.UtilisateurEnsemble;
import fr.afpa.choral_riff.mapper.UtilisateurMapper;
import fr.afpa.choral_riff.repositories.UtilisateurEnsembleRepository;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.userdetails.UserDetails;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
/**
 * 
 */
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurMapper utilisateurMapper;
    private final PasswordEncoder passwordEncoder;
    private UtilisateurEnsembleRepository utilisateurEnsembleRepository;

    public UtilisateurService(UtilisateurRepository utilisateurRepository, UtilisateurMapper utilisateurMapper,
            PasswordEncoder passwordEncoder, InvitationService invitationService,
            UtilisateurEnsembleRepository utilisateurEnsembleRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.utilisateurMapper = utilisateurMapper;
        this.passwordEncoder = passwordEncoder;
        this.utilisateurEnsembleRepository = utilisateurEnsembleRepository;
    }

    public UtilisateurDto getById(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Utilisateur non trouvé avec id " + id));
        return utilisateurMapper.toDto(utilisateur);
    }

    public UtilisateurDto register(RegisterDto dto) {
        // Vérifier si l’utilisateur existe déjà
        Optional<Utilisateur> existingUser = utilisateurRepository.findByEmail(dto.getEmail());
        if (existingUser.isPresent()) {
            // L’utilisateur existe → juste retourner le DTO
            return utilisateurMapper.toDto(existingUser.get());
        }

        // L’utilisateur n'existe pas → création normale
        Utilisateur entity = utilisateurMapper.fromRegisterDto(dto);
        entity.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        Utilisateur saved = utilisateurRepository.save(entity);

        // Rattachement via token est maintenant géré uniquement par InvitationService

        return utilisateurMapper.toDto(saved);
    }

    public List<UtilisateurDto> getAll() {
        List<Utilisateur> utilisateur = utilisateurRepository.findAll();
        return utilisateurMapper.toDtoList(utilisateur);
    }

    public UtilisateurDto create(UtilisateurDto dto) {
        Utilisateur entity = utilisateurMapper.toEntity(dto);
        // ici on peut gérer motDePasse si besoin, ex : encoder le mot de passe avant
        // save

        // Encoder le mot de passe AVANT sauvegarde
        entity.setMotDePasse(passwordEncoder.encode(entity.getMotDePasse()));
        Utilisateur saved = utilisateurRepository.save(entity);
        return utilisateurMapper.toDto(saved);
    }

    public UtilisateurDto update(Long id, UtilisateurDto dto) {
        Utilisateur entity = utilisateurRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Utilisateur non trouvé avec id " + id));
        utilisateurMapper.updateEntityFromDto(dto, entity);
        Utilisateur updated = utilisateurRepository.save(entity);
        return utilisateurMapper.toDto(updated);
    }

    public Utilisateur getByEmail(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Utilisateur non trouvé avec email : " + email));
    }

    public void delete(Long id) {
        if (!utilisateurRepository.existsById(id)) {
            throw new NoSuchElementException("Utilisateur non trouvé avec id " + id);
        }
        utilisateurRepository.deleteById(id);
    }

    public Utilisateur createFromRegisterDto(RegisterDto dto) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail(dto.getEmail());
        utilisateur.setNom(dto.getNom());
        utilisateur.setPrenom(dto.getPrenom());
        utilisateur.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));

        return utilisateurRepository.save(utilisateur);
    }

    public List<UtilisateurDto> getUtilisateursParEnsemble(Long ensembleId) {
        return utilisateurEnsembleRepository.findByEnsembleId(ensembleId).stream()
                .map(UtilisateurEnsemble::getUtilisateur) // récupère l'utilisateur depuis la relation
                .map(utilisateurMapper::toDto)
                .collect(Collectors.toList());
    }

    public UtilisateurDto toDto(Utilisateur utilisateur) {
        if (utilisateur == null)
            return null;
        return new UtilisateurDto(
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getPhotoProfil());
    }

    // public Utilisateur getCurrentUser() {
    // Object principal =
    // SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    // if (principal instanceof UserDetails userDetails) {
    // String email = userDetails.getUsername();
    // return utilisateurRepository.findByEmail(email)
    // .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    // } else {
    // throw new RuntimeException("Utilisateur non authentifié");
    // }
    // }

    public Utilisateur getCurrentUser() {
        // temporaire pour tester la récupération de la photo
        return utilisateurRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Utilisateur de test non trouvé"));
    }

    public UtilisateurDto updatePhotoProfil(@RequestParam("photoProfil") MultipartFile file) {
        Utilisateur utilisateur = getCurrentUser();
        if (file == null || file.isEmpty())
            throw new RuntimeException("Fichier vide");

        try {
            // Chemin absolu sur le serveur
            String uploadDir = "/workspaces/Choral-Riff-Backend/uploads/profil/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = utilisateur.getId() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            file.transferTo(filePath.toFile()); // fichier enregistré physiquement

            // Mettre à jour la base
            utilisateur.setPhotoProfil("/uploads/profil/" + fileName);
            utilisateur = utilisateurRepository.save(utilisateur);

            return utilisateurMapper.toDto(utilisateur);

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement de la photo", e);
        }
    }

}
