// package fr.afpa.choral_riff.services;

// import fr.afpa.choral_riff.dto.EnsembleDto;
// import fr.afpa.choral_riff.entity.Ensemble;
// import fr.afpa.choral_riff.mapper.EnsembleMapper;
// import fr.afpa.choral_riff.repositories.EnsembleRepository;
// import jakarta.persistence.EntityNotFoundException;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.*;

// import java.util.List;
// import java.util.Optional;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.anyLong;
// import static org.mockito.Mockito.*;

// class EnsembleServiceTest {

//     @Mock
//     private EnsembleRepository ensembleRepository;

//     @Mock
//     private EnsembleMapper ensembleMapper;

//     @InjectMocks
//     private EnsembleService ensembleService;

//     private Ensemble ensembleEntity;
//     private EnsembleDto ensembleDto;

//     @BeforeEach
//     void setUp() {
//         MockitoAnnotations.openMocks(this);

//         // Exemple d'entité et DTO
//         ensembleEntity = new Ensemble();
//         ensembleEntity.setId(1L);
//         ensembleEntity.setNom("Ensemble Test");

//         // DTO avec record(pas de setters)
//         ensembleDto = new EnsembleDto(1L, "Ensemble Test", null, null);
//     }

//     @Test
//     void testGetAll() {
//         when(ensembleRepository.findAll()).thenReturn(List.of(ensembleEntity));
//         when(ensembleMapper.toDto(ensembleEntity)).thenReturn(ensembleDto);

//         List<EnsembleDto> result = ensembleService.getAll();

//         assertNotNull(result);
//         assertEquals(1, result.size());
//         assertEquals("Ensemble Test", result.get(0).getNom());

//         verify(ensembleRepository, times(1)).findAll();
//         verify(ensembleMapper, times(1)).toDto(ensembleEntity);
//     }

//     @Test
//     void testCreate() {
//         Long userId = 42L; // exemple d'utilisateur

//         when(ensembleMapper.toEntity(ensembleDto)).thenReturn(ensembleEntity);
//         when(ensembleRepository.save(ensembleEntity)).thenReturn(ensembleEntity);
//         when(ensembleMapper.toDto(ensembleEntity)).thenReturn(ensembleDto);

//         EnsembleDto result = ensembleService.create(ensembleDto, userId);

//         assertNotNull(result);
//         assertEquals(ensembleDto.getNom(), result.getNom());

//         verify(ensembleMapper).toEntity(ensembleDto);
//         verify(ensembleRepository).save(ensembleEntity);
//         verify(ensembleMapper).toDto(ensembleEntity);
//     }

//     @Test
//     void testGetByIdFound() {
//         when(ensembleRepository.findById(1L)).thenReturn(Optional.of(ensembleEntity));
//         when(ensembleMapper.toDto(ensembleEntity)).thenReturn(ensembleDto);

//         EnsembleDto result = ensembleService.getById(1L);

//         assertNotNull(result);
//         assertEquals("Ensemble Test", result.getNom());

//         verify(ensembleRepository).findById(1L);
//         verify(ensembleMapper).toDto(ensembleEntity);
//     }

//     @Test
//     void testGetByIdNotFound() {
//         when(ensembleRepository.findById(1L)).thenReturn(Optional.empty());

//         EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
//                 () -> ensembleService.getById(1L));

//         assertEquals("Ensemble non trouvé avec l’ID : 1", exception.getMessage());

//         verify(ensembleRepository).findById(1L);
//         verifyNoMoreInteractions(ensembleMapper);
//     }

//     @Test
//     void testUpdateSuccess() {
//         when(ensembleRepository.findById(1L)).thenReturn(Optional.of(ensembleEntity));

//         // Pas besoin de stub pour updateEntityFromDto car void, on vérifie appel
//         doNothing().when(ensembleMapper).updateEntityFromDto(ensembleDto, ensembleEntity);

//         when(ensembleRepository.save(ensembleEntity)).thenReturn(ensembleEntity);
//         when(ensembleMapper.toDto(ensembleEntity)).thenReturn(ensembleDto);

//         EnsembleDto updated = ensembleService.update(1L, ensembleDto);

//         assertNotNull(updated);
//         assertEquals("Ensemble Test", updated.getNom());

//         verify(ensembleRepository).findById(1L);
//         verify(ensembleMapper).updateEntityFromDto(ensembleDto, ensembleEntity);
//         verify(ensembleRepository).save(ensembleEntity);
//         verify(ensembleMapper).toDto(ensembleEntity);
//     }

//     @Test
//     void testUpdateNotFound() {
//         when(ensembleRepository.findById(1L)).thenReturn(Optional.empty());

//         EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
//                 () -> ensembleService.update(1L, ensembleDto));

//         assertEquals("Impossible de mettre à jour : ID 1 introuvable", exception.getMessage());

//         verify(ensembleRepository).findById(1L);
//         verifyNoMoreInteractions(ensembleMapper);
//     }

//     @Test
//     void testDeleteSuccess() {
//         when(ensembleRepository.existsById(1L)).thenReturn(true);
//         doNothing().when(ensembleRepository).deleteById(1L);

//         assertDoesNotThrow(() -> ensembleService.delete(1L));

//         verify(ensembleRepository).existsById(1L);
//         verify(ensembleRepository).deleteById(1L);
//     }

//     @Test
//     void testDeleteNotFound() {
//         when(ensembleRepository.existsById(1L)).thenReturn(false);

//         EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
//                 () -> ensembleService.delete(1L));

//         assertEquals("Impossible de supprimer : ensemble avec ID 1 introuvable", exception.getMessage());

//         verify(ensembleRepository).existsById(1L);
//         verify(ensembleRepository, never()).deleteById(anyLong());
//     }
// }
