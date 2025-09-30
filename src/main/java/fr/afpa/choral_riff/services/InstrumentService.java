package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.dto.InstrumentDto;
import fr.afpa.choral_riff.entity.Ensemble;
import fr.afpa.choral_riff.entity.Instrument;
import fr.afpa.choral_riff.mapper.InstrumentMapper;
import fr.afpa.choral_riff.repositories.EnsembleRepository;
import fr.afpa.choral_riff.repositories.InstrumentRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InstrumentService {

    private final InstrumentRepository instrumentRepository;
    private final EnsembleRepository ensembleRepository;
    private final InstrumentMapper instrumentMapper;

    public InstrumentService(InstrumentRepository instrumentRepository,
            EnsembleRepository ensembleRepository,
            InstrumentMapper instrumentMapper) {
        this.instrumentRepository = instrumentRepository;
        this.ensembleRepository = ensembleRepository;
        this.instrumentMapper = instrumentMapper;
    }

    // Récupérer tous les instruments
    public List<InstrumentDto> getAll() {
        return instrumentRepository.findAll().stream()
                .map(instrumentMapper::toDto)
                .collect(Collectors.toList());
    }

    // Récupérer un instrument par ID
    public InstrumentDto getById(Long id) {
        Instrument instrument = instrumentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Instrument non trouvé avec l'ID: " + id));
        return instrumentMapper.toDto(instrument);
    }

    // Créer un instrument avec validation de l’ensemble
    public InstrumentDto create(InstrumentDto dto, Long ensembleId) {
        Instrument instrument = instrumentMapper.toEntity(dto);

        if (ensembleId != null) {
            Ensemble ensemble = ensembleRepository.findById(ensembleId)
                    .orElseThrow(() -> new RuntimeException("Ensemble non trouvé avec l'ID: " + ensembleId));
            instrument.setEnsemble(ensemble);
        }

        Instrument saved = instrumentRepository.save(instrument);
        return instrumentMapper.toDto(saved);
    }

    // Mettre à jour un instrument existant
    public InstrumentDto update(Long id, InstrumentDto dto, Long ensembleId) {
        Instrument instrument = instrumentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Instrument non trouvé avec l'ID: " + id));

        instrumentMapper.updateEntityFromDto(dto, instrument);

        if (ensembleId != null) {
            Ensemble ensemble = ensembleRepository.findById(ensembleId)
                    .orElseThrow(() -> new RuntimeException("Ensemble non trouvé avec l'ID: " + ensembleId));
            instrument.setEnsemble(ensemble);
        }

        Instrument updated = instrumentRepository.save(instrument);
        return instrumentMapper.toDto(updated);
    }

    // Supprimer un instrument par ID
    public void delete(Long id) {
        if (!instrumentRepository.existsById(id)) {
            throw new RuntimeException("Instrument non trouvé avec l'ID: " + id);
        }
        instrumentRepository.deleteById(id);
    }
}
