package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.dto.EnsembleDto;


import fr.afpa.choral_riff.entity.Ensemble;
import fr.afpa.choral_riff.mapper.EnsembleMapper;
import fr.afpa.choral_riff.repositories.EnsembleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service métier pour gérer les opérations liées aux ensembles musicaux.
 * 
 * Ce service permet de :
 * 
 * Créer un nouvel ensemble
 * Récupérer tous les ensembles existants
 * Récupérer un ensemble par son identifiant
 * Mettre à jour un ensemble existant
 * Supprimer un ensemble
 * 
 *
 * Utilise :
 * 
 * EnsembleRepository : accès à la base de données
 * EnsembleMapper : conversion entre Entity et DTO
 * 
 *
 * 
 */
@Service
public class EnsembleService {

    private final EnsembleRepository ensembleRepository;
    private final EnsembleMapper ensembleMapper;

    /**
     * Constructeur avec injection des dépendances.
     *
     * @param ensembleRepository le repository pour l'entité Ensemble
     * @param ensembleMapper     le mapper pour convertir entre Entity et DTO
     */
    public EnsembleService(EnsembleRepository ensembleRepository, EnsembleMapper ensembleMapper) {
        this.ensembleRepository = ensembleRepository;
        this.ensembleMapper = ensembleMapper;
    }

    /**
     * Récupère tous les ensembles enregistrés.
     *
     * @return une liste de DTO représentant les ensembles
     */
    public List<EnsembleDto> getAll() {
        return ensembleRepository.findAll().stream()
                .map(ensembleMapper::toDto)
                .toList();
    }

    /**
     * Crée un nouvel ensemble à partir d’un DTO.
     *
     * @param dto le DTO contenant les données du nouvel ensemble
     * @return le DTO de l’ensemble créé
     */
    public EnsembleDto create(EnsembleDto dto) {
        Ensemble ensemble = ensembleMapper.toEntity(dto);
        Ensemble saved = ensembleRepository.save(ensemble);
        return ensembleMapper.toDto(saved);
    }

    /**
     * Récupère un ensemble par son identifiant.
     *
     * @param id l'identifiant de l’ensemble
     * @return le DTO correspondant
     * @throws EntityNotFoundException si aucun ensemble n’a été trouvé
     */
    public EnsembleDto getById(Long id) {
        Ensemble ensemble = ensembleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ensemble non trouvé avec l’ID : " + id));
        return ensembleMapper.toDto(ensemble);
    }

    /**
     * Met à jour les informations d’un ensemble existant.
     *
     * @param id  l'identifiant de l’ensemble à mettre à jour
     * @param dto le DTO contenant les nouvelles données
     * @return le DTO de l’ensemble mis à jour
     * @throws EntityNotFoundException si aucun ensemble avec cet ID n’existe
     */
    public EnsembleDto update(Long id, EnsembleDto dto) {
        Ensemble existing = ensembleRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Impossible de mettre à jour : ID " + id + " introuvable"));

        ensembleMapper.updateEntityFromDto(dto, existing);
        Ensemble updated = ensembleRepository.save(existing);
        return ensembleMapper.toDto(updated);
    }

    /**
     * Supprime un ensemble existant par son identifiant.
     *
     * @param id l’identifiant de l’ensemble à supprimer
     * @throws EntityNotFoundException si aucun ensemble avec cet ID n’existe
     */
    public void delete(Long id) {
        if (!ensembleRepository.existsById(id)) {
            throw new EntityNotFoundException("Impossible de supprimer : ensemble avec ID " + id + " introuvable");
        }
        ensembleRepository.deleteById(id);
    }






    
}
