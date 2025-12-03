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
import java.util.List;
import java.util.NoSuchElementException;
import fr.afpa.choral_riff.services.InvitationService;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
/**
 * 
 */
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurMapper utilisateurMapper;
    private final PasswordEncoder passwordEncoder;
    private final InvitationService invitationService;
    private UtilisateurEnsembleRepository utilisateurEnsembleRepository;

    public UtilisateurService(UtilisateurRepository utilisateurRepository, UtilisateurMapper utilisateurMapper,
            PasswordEncoder passwordEncoder, InvitationService invitationService,
            UtilisateurEnsembleRepository utilisateurEnsembleRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.utilisateurMapper = utilisateurMapper;
        this.passwordEncoder = passwordEncoder;
        this.invitationService = invitationService;

    }

    public UtilisateurDto getById(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Utilisateur non trouvé avec id " + id));
        return utilisateurMapper.toDto(utilisateur);
    }

    // public UtilisateurDto register(RegisterDto dto) {

    // // Vérifier si l’utilisateur existe déjà
    // Optional<Utilisateur> existingUser =
    // utilisateurRepository.findByEmail(dto.getEmail());

    // Utilisateur utilisateur;

    // if (existingUser.isPresent()) {
    // // L’utilisateur existe déjà → ne pas le recréer
    // utilisateur = existingUser.get();

    // // Si token présent → rattacher à l'ensemble
    // if (dto.getToken() != null && !dto.getToken().isEmpty()) {
    // invitationService.rattacherUtilisateurApresInscription(dto.getToken(),
    // utilisateur);
    // }

    // return utilisateurMapper.toDto(utilisateur);
    // }

    // // L’utilisateur n'existe pas → création normale
    // Utilisateur entity = utilisateurMapper.fromRegisterDto(dto);
    // entity.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
    // Utilisateur saved = utilisateurRepository.save(entity);

    // // Si un token existe → rattacher
    // if (dto.getToken() != null && !dto.getToken().isEmpty()) {
    // invitationService.rattacherUtilisateurApresInscription(dto.getToken(),
    // saved);
    // }

    // return utilisateurMapper.toDto(saved);
    // }

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
        // ici tu peux gérer motDePasse si besoin, ex : encoder le mot de passe avant
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

}
