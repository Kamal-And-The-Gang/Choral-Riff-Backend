package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.dto.UtilisateurDto;
import fr.afpa.choral_riff.entity.Utilisateur;
import fr.afpa.choral_riff.mapper.UtilisateurMapper;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurMapper utilisateurMapper;

    public UtilisateurService(UtilisateurRepository utilisateurRepository, UtilisateurMapper utilisateurMapper) {
        this.utilisateurRepository = utilisateurRepository;
        this.utilisateurMapper = utilisateurMapper;
    }

    public UtilisateurDto getById(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Utilisateur non trouvé avec id " + id));
        return utilisateurMapper.toDto(utilisateur);
    }

    public List<UtilisateurDto> getAll() {
        List<Utilisateur> utilisateurs = utilisateurRepository.findAll();
        return utilisateurMapper.toDtoList(utilisateurs);
    }

    public UtilisateurDto create(UtilisateurDto dto) {
        Utilisateur entity = utilisateurMapper.toEntity(dto);
        // ici tu peux gérer motDePasse si besoin, ex : encoder le mot de passe avant
        // save
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
