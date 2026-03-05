// package fr.afpa.choral_riff.services;

// import fr.afpa.choral_riff.dto.InstrumentDto;
// import fr.afpa.choral_riff.entity.Document;
// import fr.afpa.choral_riff.entity.Instrument;
// import fr.afpa.choral_riff.entity.Morceau;
// import fr.afpa.choral_riff.entity.Ensemble;
// import fr.afpa.choral_riff.mapper.InstrumentMapper;
// import fr.afpa.choral_riff.repositories.DocumentRepository;
// import fr.afpa.choral_riff.repositories.InstrumentRepository;
// import fr.afpa.choral_riff.repositories.MorceauRepository;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import java.util.HashSet;
// import java.util.List;
// import java.util.Optional;
// import java.util.Set;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class)
// class InstrumentServiceTest {

//     @Mock
//     private InstrumentRepository instrumentRepository;

//     @Mock
//     private MorceauRepository morceauRepository;

//     @Mock
//     private DocumentRepository documentRepository;

//     @Mock
//     private InstrumentMapper instrumentMapper;

//     @Mock
//     private UtilisateurEnsembleService utilisateurEnsembleService;

//     @InjectMocks
//     private InstrumentService instrumentService;

//     private Instrument instrument;
//     private Morceau morceau;
//     private Document document;
//     private Ensemble ensemble;
//     private InstrumentDto instrumentDto;

//     @BeforeEach
//     void setUp() {
//         // Création de l'ensemble
//         ensemble = new Ensemble();
//         ensemble.setId(100L);

//         // Création du morceau
//         morceau = new Morceau();
//         morceau.setId(10L);
//         morceau.setEnsemble(ensemble);

//         // Création du document
//         document = new Document();
//         document.setId(1L);
//         document.setMorceau(morceau);

//         // Création de l’instrument
//         instrument = new Instrument();
//         instrument.setId(5L);
//         instrument.setNom("Piano");

//         // Simuler relation Many-to-Many via getDocuments()
//         Set<Document> docs = new HashSet<>();
//         docs.add(document);
//         instrument = spy(instrument);
//         lenient().doReturn(docs).when(instrument).getDocuments();

//         // DTO correspondant
//         instrumentDto = new InstrumentDto(5L, "Piano", Set.of(1L));

//         // Mapper stubs
//         lenient().when(instrumentMapper.toDto(any(Instrument.class))).thenAnswer(invocation -> {
//             Instrument inst = invocation.getArgument(0);
//             Set<Long> documentIds = new HashSet<>();
//             if (inst.getDocuments() != null) {
//                 inst.getDocuments().forEach(d -> documentIds.add(d.getId()));
//             }
//             return new InstrumentDto(inst.getId(), inst.getNom(), documentIds);
//         });

//         lenient().doAnswer(invocation -> {
//             InstrumentDto dto = invocation.getArgument(0);
//             Instrument inst = invocation.getArgument(1);
//             inst.setNom(dto.nom());
//             return null;
//         }).when(instrumentMapper).updateEntityFromDto(any(InstrumentDto.class), any(Instrument.class));
//     }

//     @Test
//     void shouldReturnAllInstruments() {
//         when(instrumentRepository.findAll()).thenReturn(List.of(instrument));

//         List<InstrumentDto> result = instrumentService.getAll();

//         assertNotNull(result);
//         assertEquals(1, result.size());
//         assertEquals("Piano", result.get(0).nom());
//     }

//     @Test
//     void shouldReturnInstrumentById() {
//         when(instrumentRepository.findById(5L)).thenReturn(Optional.of(instrument));

//         InstrumentDto dto = instrumentService.getById(5L);

//         assertNotNull(dto);
//         assertEquals("Piano", dto.nom());
//     }

//     @Test
//     void shouldThrowWhenInstrumentNotFound() {
//         when(instrumentRepository.findById(99L)).thenReturn(Optional.empty());

//         RuntimeException exception = assertThrows(RuntimeException.class,
//                 () -> instrumentService.getById(99L));
//         assertEquals("Instrument non trouvé avec l'ID: 99", exception.getMessage());
//     }

//     @Test
//     void shouldDeleteInstrument() {
//         when(instrumentRepository.existsById(5L)).thenReturn(true);

//         instrumentService.delete(5L);

//         verify(instrumentRepository).deleteById(5L);
//     }

//     @Test
//     void shouldThrowWhenDeletingNonExistingInstrument() {
//         when(instrumentRepository.existsById(99L)).thenReturn(false);

//         RuntimeException exception = assertThrows(RuntimeException.class,
//                 () -> instrumentService.delete(99L));
//         assertEquals("Instrument non trouvé avec l'ID: 99", exception.getMessage());
//     }

//     @Test
//     void shouldAddInstrumentToMorceau() {
//         when(morceauRepository.findById(10L)).thenReturn(Optional.of(morceau));
//         when(instrumentRepository.findById(5L)).thenReturn(Optional.of(instrument));

//         InstrumentDto dto = instrumentService.addInstrumentToMorceau(10L, 5L);

//         assertNotNull(dto);
//         assertEquals("Piano", dto.nom());
//     }

//     @Test
//     void shouldUpdateInstrumentWithoutRightsCheck() {
//         when(instrumentRepository.findById(5L)).thenReturn(Optional.of(instrument));
//         when(instrumentRepository.save(any())).thenReturn(instrument);

//         InstrumentDto dto = instrumentService.update(5L, instrumentDto);

//         assertNotNull(dto);
//         assertEquals("Piano", dto.nom());
//         verify(instrumentRepository).save(instrument);
//     }

//     @Test
//     void shouldUpdateInstrumentWithRightsCheckWhenAuthorized() {
//         when(instrumentRepository.findById(5L)).thenReturn(Optional.of(instrument));
//         when(instrumentRepository.save(any())).thenReturn(instrument);
//         when(utilisateurEnsembleService.utilisateurAutorise(1L, 100L, List.of("ADMIN", "MODERATEUR"))).thenReturn(true);

//         InstrumentDto dto = instrumentService.update(1L, 5L, instrumentDto);

//         assertNotNull(dto);
//         assertEquals("Piano", dto.nom());
//     }

//     @Test
//     void shouldThrowUpdateInstrumentWithRightsCheckWhenNotAuthorized() {
//         when(instrumentRepository.findById(5L)).thenReturn(Optional.of(instrument));
//         when(utilisateurEnsembleService.utilisateurAutorise(1L, 100L, List.of("ADMIN", "MODERATEUR"))).thenReturn(false);

//         RuntimeException exception = assertThrows(RuntimeException.class,
//                 () -> instrumentService.update(1L, 5L, instrumentDto));
//         assertEquals("Vous n'avez pas les droits pour modifier cet instrument", exception.getMessage());
//     }
// }
