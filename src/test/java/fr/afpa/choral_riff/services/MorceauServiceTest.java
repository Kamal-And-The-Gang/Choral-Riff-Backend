// package fr.afpa.choral_riff.services;

// import fr.afpa.choral_riff.dto.MorceauDto;
// import fr.afpa.choral_riff.entity.Ensemble;
// import fr.afpa.choral_riff.entity.Morceau;
// import fr.afpa.choral_riff.entity.Utilisateur;
// import fr.afpa.choral_riff.mapper.MorceauMapper;
// import fr.afpa.choral_riff.repositories.EnsembleRepository;
// import fr.afpa.choral_riff.repositories.MorceauRepository;
// import fr.afpa.choral_riff.repositories.UtilisateurRepository;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.*;

// import java.util.List;
// import java.util.Optional;
// import java.util.Set;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;

// class MorceauServiceTest {

//     @Mock
//     private MorceauRepository morceauRepository;

//     @Mock
//     private EnsembleRepository ensembleRepository;

//     @Mock
//     private UtilisateurRepository utilisateurRepository;

//     @Mock
//     private MorceauMapper morceauMapper;

//     @InjectMocks
//     private MorceauService morceauService;

//     @BeforeEach
//     void setup() {
//         MockitoAnnotations.openMocks(this);
//     }

//     @Test
//     void testGetAll_ReturnsDtoList() {
//         Morceau morceau = new Morceau();
//         morceau.setId(1L);
//         List<Morceau> morceaux = List.of(morceau);

//         MorceauDto dto = new MorceauDto(1L, "Titre", "Compositeur", "Genre", "Desc", 2L, 3L, Set.of());

//         when(morceauRepository.findAll()).thenReturn(morceaux);
//         when(morceauMapper.toDto(morceau)).thenReturn(dto);

//         List<MorceauDto> result = morceauService.getAll();

//         assertNotNull(result);
//         assertEquals(1, result.size());
//         assertEquals(dto, result.get(0));

//         verify(morceauRepository).findAll();
//         verify(morceauMapper).toDto(morceau);
//     }

//     @Test
//     void testGetById_Found_ReturnsDto() {
//         Morceau morceau = new Morceau();
//         morceau.setId(1L);

//         MorceauDto dto = new MorceauDto(1L, "Titre", "Comp", "Genre", "Desc", 2L, 3L, Set.of());

//         when(morceauRepository.findById(1L)).thenReturn(Optional.of(morceau));
//         when(morceauMapper.toDto(morceau)).thenReturn(dto);

//         MorceauDto result = morceauService.getById(1L);

//         assertNotNull(result);
//         assertEquals(1L, result.id());

//         verify(morceauRepository).findById(1L);
//         verify(morceauMapper).toDto(morceau);
//     }

//     @Test
//     void testGetById_NotFound_Throws() {
//         when(morceauRepository.findById(1L)).thenReturn(Optional.empty());

//         RuntimeException ex = assertThrows(RuntimeException.class, () -> morceauService.getById(1L));
//         assertTrue(ex.getMessage().contains("non trouvé"));

//         verify(morceauRepository).findById(1L);
//         verifyNoMoreInteractions(morceauMapper);
//     }

//     @Test
//     void testCreate_WithRelations_Success() {
//         MorceauDto dto = new MorceauDto(null, "Titre", "Comp", "Genre", "Desc", 5L, 7L, Set.of());
//         Morceau morceauEntity = new Morceau();
//         Morceau savedEntity = new Morceau();
//         savedEntity.setId(10L);

//         Ensemble ensemble = new Ensemble();
//         ensemble.setId(5L);

//         Utilisateur createur = new Utilisateur();
//         createur.setId(7L);

//         MorceauDto savedDto = new MorceauDto(10L, "Titre", "Comp", "Genre", "Desc", 5L, 7L, Set.of());

//         when(morceauMapper.toEntity(dto)).thenReturn(morceauEntity);
//         when(ensembleRepository.findById(5L)).thenReturn(Optional.of(ensemble));
//         when(utilisateurRepository.findById(7L)).thenReturn(Optional.of(createur));
//         when(morceauRepository.save(morceauEntity)).thenReturn(savedEntity);
//         when(morceauMapper.toDto(savedEntity)).thenReturn(savedDto);

//         MorceauDto result = morceauService.create(dto);

//         assertNotNull(result);
//         assertEquals(10L, result.id());
//         assertEquals("Titre", result.titre());

//         verify(morceauMapper).toEntity(dto);
//         verify(ensembleRepository).findById(5L);
//         verify(utilisateurRepository).findById(7L);
//         verify(morceauRepository).save(morceauEntity);
//         verify(morceauMapper).toDto(savedEntity);
//     }

//     @Test
//     void testUpdate_ExistingMorceau_Success() {
//         Long id = 20L;
//         MorceauDto dto = new MorceauDto(null, "NewTitre", "NewComp", "NewGenre", "NewDesc", 5L, 7L, Set.of());
//         Morceau morceauEntity = new Morceau();
//         morceauEntity.setId(id);

//         Ensemble ensemble = new Ensemble();
//         ensemble.setId(5L);

//         Utilisateur createur = new Utilisateur();
//         createur.setId(7L);

//         Morceau updatedEntity = new Morceau();
//         updatedEntity.setId(id);

//         MorceauDto updatedDto = new MorceauDto(id, "NewTitre", "NewComp", "NewGenre", "NewDesc", 5L, 7L, Set.of());

//         when(morceauRepository.findById(id)).thenReturn(Optional.of(morceauEntity));
//         when(ensembleRepository.findById(5L)).thenReturn(Optional.of(ensemble));
//         when(utilisateurRepository.findById(7L)).thenReturn(Optional.of(createur));
//         when(morceauRepository.save(morceauEntity)).thenReturn(updatedEntity);
//         when(morceauMapper.toDto(updatedEntity)).thenReturn(updatedDto);

//         MorceauDto result = morceauService.update(id, dto);

//         assertNotNull(result);
//         assertEquals(id, result.id());
//         assertEquals("NewTitre", result.titre());

//         verify(morceauRepository).findById(id);
//         verify(ensembleRepository).findById(5L);
//         verify(utilisateurRepository).findById(7L);
//         verify(morceauRepository).save(morceauEntity);
//         verify(morceauMapper).toDto(updatedEntity);
//     }

//     @Test
//     void testUpdate_NotFound_Throws() {
//         Long id = 1L;
//         MorceauDto dto = new MorceauDto(null, "Titre", null, null, null, null, null, Set.of());

//         when(morceauRepository.findById(id)).thenReturn(Optional.empty());

//         RuntimeException ex = assertThrows(RuntimeException.class, () -> morceauService.update(id, dto));
//         assertTrue(ex.getMessage().contains("non trouvé"));

//         verify(morceauRepository).findById(id);
//         verifyNoMoreInteractions(ensembleRepository, utilisateurRepository, morceauRepository, morceauMapper);
//     }

//     @Test
//     void testDelete_Exists_Success() {
//         Long id = 3L;
//         when(morceauRepository.existsById(id)).thenReturn(true);

//         morceauService.delete(id);

//         verify(morceauRepository).existsById(id);
//         verify(morceauRepository).deleteById(id);
//     }

//     @Test
//     void testDelete_NotExists_Throws() {
//         Long id = 3L;
//         when(morceauRepository.existsById(id)).thenReturn(false);

//         RuntimeException ex = assertThrows(RuntimeException.class, () -> morceauService.delete(id));
//         assertTrue(ex.getMessage().contains("non trouvé"));

//         verify(morceauRepository).existsById(id);
//         verify(morceauRepository, never()).deleteById(any());
//     }
// }
