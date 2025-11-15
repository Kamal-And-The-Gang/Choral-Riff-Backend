package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.dto.InstrumentDto;
import fr.afpa.choral_riff.entity.Ensemble;
import fr.afpa.choral_riff.entity.Instrument;
import fr.afpa.choral_riff.mapper.InstrumentMapper;
import fr.afpa.choral_riff.repositories.EnsembleRepository;
import fr.afpa.choral_riff.repositories.InstrumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InstrumentServiceTest {

    private InstrumentRepository instrumentRepository;
    private EnsembleRepository ensembleRepository;
    private InstrumentMapper instrumentMapper;
    private InstrumentService instrumentService;

    @BeforeEach
    public void setUp() {
        instrumentRepository = mock(InstrumentRepository.class);
        ensembleRepository = mock(EnsembleRepository.class);
        instrumentMapper = mock(InstrumentMapper.class);
        instrumentService = new InstrumentService(instrumentRepository, ensembleRepository, instrumentMapper);
    }

    @Test
    public void testGetAll() {
        Instrument instrument = new Instrument();
        instrument.setId(1L);
        instrument.setNom("Guitare");

        InstrumentDto dto = new InstrumentDto(1L, "Guitare", null);

        when(instrumentRepository.findAll()).thenReturn(List.of(instrument));
        when(instrumentMapper.toDto(instrument)).thenReturn(dto);

        List<InstrumentDto> result = instrumentService.getAll();

        assertEquals(1, result.size());
        assertEquals("Guitare", result.get(0).nom());
        verify(instrumentRepository).findAll();
        verify(instrumentMapper).toDto(instrument);
    }

    @Test
    public void testGetById_Found() {
        Instrument instrument = new Instrument();
        instrument.setId(2L);
        instrument.setNom("Piano");

        InstrumentDto dto = new InstrumentDto(2L, "Piano", null);

        when(instrumentRepository.findById(2L)).thenReturn(Optional.of(instrument));
        when(instrumentMapper.toDto(instrument)).thenReturn(dto);

        InstrumentDto result = instrumentService.getById(2L);

        assertNotNull(result);
        assertEquals(2L, result.id());
        assertEquals("Piano", result.nom());
    }

    @Test
    public void testGetById_NotFound() {
        when(instrumentRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> instrumentService.getById(99L));
        assertTrue(exception.getMessage().contains("Instrument non trouvé"));
    }

    @Test
    public void testCreate_WithEnsemble() {
        InstrumentDto dto = new InstrumentDto(null, "Violoncelle", null);
        Ensemble ensemble = new Ensemble();
        ensemble.setId(5L);

        Instrument instrumentEntity = new Instrument();
        Instrument savedInstrument = new Instrument();
        savedInstrument.setId(10L);
        savedInstrument.setNom("Violoncelle");
        savedInstrument.setEnsemble(ensemble);

        InstrumentDto savedDto = new InstrumentDto(10L, "Violoncelle", null);

        when(instrumentMapper.toEntity(dto)).thenReturn(instrumentEntity);
        when(ensembleRepository.findById(5L)).thenReturn(Optional.of(ensemble));
        when(instrumentRepository.save(any(Instrument.class))).thenReturn(savedInstrument);
        when(instrumentMapper.toDto(savedInstrument)).thenReturn(savedDto);

        InstrumentDto result = instrumentService.create(dto, 5L);

        assertNotNull(result);
        assertEquals(10L, result.id());
        assertEquals("Violoncelle", result.nom());
        verify(instrumentMapper).toEntity(dto);
        verify(ensembleRepository).findById(5L);
        verify(instrumentRepository).save(any());
        verify(instrumentMapper).toDto(savedInstrument);
    }

    @Test
    public void testUpdate_WithEnsemble() {
        InstrumentDto dto = new InstrumentDto(null, "Flûte traversière", null);
        Ensemble ensemble = new Ensemble();
        ensemble.setId(7L);

        Instrument existingInstrument = new Instrument();
        existingInstrument.setId(20L);
        existingInstrument.setNom("Flûte");

        Instrument updatedInstrument = new Instrument();
        updatedInstrument.setId(20L);
        updatedInstrument.setNom("Flûte traversière");
        updatedInstrument.setEnsemble(ensemble);

        InstrumentDto updatedDto = new InstrumentDto(20L, "Flûte traversière", null);

        when(instrumentRepository.findById(20L)).thenReturn(Optional.of(existingInstrument));
        doNothing().when(instrumentMapper).updateEntityFromDto(dto, existingInstrument);
        when(ensembleRepository.findById(7L)).thenReturn(Optional.of(ensemble));
        when(instrumentRepository.save(existingInstrument)).thenReturn(updatedInstrument);
        when(instrumentMapper.toDto(updatedInstrument)).thenReturn(updatedDto);

        InstrumentDto result = instrumentService.update(20L, dto, 7L);

        assertNotNull(result);
        assertEquals(20L, result.id());
        assertEquals("Flûte traversière", result.nom());
        verify(instrumentRepository).findById(20L);
        verify(instrumentMapper).updateEntityFromDto(dto, existingInstrument);
        verify(ensembleRepository).findById(7L);
        verify(instrumentRepository).save(existingInstrument);
        verify(instrumentMapper).toDto(updatedInstrument);
    }

    @Test
    public void testDelete_Exists() {
        when(instrumentRepository.existsById(15L)).thenReturn(true);
        doNothing().when(instrumentRepository).deleteById(15L);

        instrumentService.delete(15L);

        verify(instrumentRepository).existsById(15L);
        verify(instrumentRepository).deleteById(15L);
    }

    @Test
    public void testDelete_NotExists() {
        when(instrumentRepository.existsById(999L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> instrumentService.delete(999L));
        assertTrue(exception.getMessage().contains("Instrument non trouvé"));

        verify(instrumentRepository).existsById(999L);
        verify(instrumentRepository, never()).deleteById(anyLong());
    }
}
