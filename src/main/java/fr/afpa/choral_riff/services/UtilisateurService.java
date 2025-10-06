package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.dto.RegisterDto;
import fr.afpa.choral_riff.dto.UtilisateurDto;
import fr.afpa.choral_riff.entity.Utilisateur;

import fr.afpa.choral_riff.mapper.UtilisateurMapper;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurMapper utilisateurMapper;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurService(UtilisateurRepository utilisateurRepository, UtilisateurMapper utilisateurMapper,
            PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.utilisateurMapper = utilisateurMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UtilisateurDto getById(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Utilisateur non trouvé avec id " + id));
        return utilisateurMapper.toDto(utilisateur);
    }

     public UtilisateurDto register(RegisterDto dto) {
        Utilisateur entity = utilisateurMapper.fromRegisterDto(dto);
        entity.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        Utilisateur saved = utilisateurRepository.save(entity);
        return utilisateurMapper.toDto(saved);
    }

    public List<UtilisateurDto> getAll() {
        List<Utilisateur> utilisateurs = utilisateurRepository.findAll();
        return utilisateurMapper.toDtoList(utilisateurs);
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

    public void delete(Long id) {
        if (!utilisateurRepository.existsById(id)) {
            throw new NoSuchElementException("Utilisateur non trouvé avec id " + id);
        }
        utilisateurRepository.deleteById(id);
    }
}
