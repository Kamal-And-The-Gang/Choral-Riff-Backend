package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.dto.InstrumentDto;
import fr.afpa.choral_riff.entity.Document;
import fr.afpa.choral_riff.entity.Instrument;
import fr.afpa.choral_riff.mapper.InstrumentMapper;
import fr.afpa.choral_riff.repositories.DocumentRepository;
import fr.afpa.choral_riff.repositories.InstrumentRepository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import fr.afpa.choral_riff.services.NotificationService;
import fr.afpa.choral_riff.entity.Utilisateur;

@Service
public class DocumentInstrumentService {

    private final DocumentRepository documentRepository;
    private final InstrumentRepository instrumentRepository;
    private final UtilisateurEnsembleService utilisateurEnsembleService;
    private final NotificationService notificationService; 

   public DocumentInstrumentService(
        DocumentRepository documentRepository,
        InstrumentRepository instrumentRepository,
        NotificationService notificationService,
        UtilisateurEnsembleService utilisateurEnsembleService // 👈 ajouté
) {
    this.documentRepository = documentRepository;
    this.instrumentRepository = instrumentRepository;
    this.notificationService = notificationService;
    this.utilisateurEnsembleService = utilisateurEnsembleService; // 👈 initialisation
}


    /**
     * Ajoute un instrument à un document
     */
//     public void addInstrumentToDocument(Long documentId, Long instrumentId) {
//         Document document = documentRepository.findById(documentId)
//                 .orElseThrow(() -> new RuntimeException("Document non trouvé : " + documentId));

//         Instrument instrument = instrumentRepository.findById(instrumentId)
//                 .orElseThrow(() -> new RuntimeException("Instrument non trouvé : " + instrumentId));

//         // Utilise la méthode utilitaire de Document
//         document.addInstrument(instrument);

//         documentRepository.save(document);
//     }


// public void addInstrumentToDocument(
//         Long documentId,
//         Long instrumentId,
//         Utilisateur utilisateur 
// ) {
//     Document document = documentRepository.findById(documentId)
//             .orElseThrow(() -> new RuntimeException("Document non trouvé : " + documentId));

//     Instrument instrument = instrumentRepository.findById(instrumentId)
//             .orElseThrow(() -> new RuntimeException("Instrument non trouvé : " + instrumentId));

//     document.addInstrument(instrument);
//     documentRepository.save(document);

//     //  Création de la notification
//     notificationService.notifierInstrumentAjoute(
//             utilisateur,
//             instrument.getNom(),
//             documentId
//     );
// }


public void addInstrumentToDocument(Long documentId, Long instrumentId, Utilisateur utilisateur) {
    Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Document non trouvé : " + documentId));

    // Vérification : l'utilisateur est le créateur du document OU a un rôle élevé
    boolean isCreateur = document.getUtilisateur().getId().equals(utilisateur.getId());

    // Si tu as besoin d’admins/modérateurs dans l’ensemble du morceau
    Long ensembleId = document.getMorceau().getEnsemble().getId();
    boolean adminOuModerateur = utilisateurEnsembleService.utilisateurAutorise(
            utilisateur.getId(),
            ensembleId,
            List.of("ADMIN", "MODERATEUR")
    );

    if (!isCreateur && !adminOuModerateur) {
        throw new AccessDeniedException(
                "Vous n'avez pas le droit d'ajouter un instrument à ce document");
    }

    Instrument instrument = instrumentRepository.findById(instrumentId)
            .orElseThrow(() -> new RuntimeException("Instrument non trouvé"));

    document.addInstrument(instrument);
    documentRepository.save(document);

    // Notification (optionnel)
    notificationService.notifierInstrumentAjoute(utilisateur, instrument.getNom(), documentId);
}



    /**
     * Récupère les instruments liés à un document sous forme de DTO
     */
    public Set<InstrumentDto> getInstrumentsByDocument(Long documentId, InstrumentMapper mapper) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document non trouvé : " + documentId));

        // Utilise la méthode utilitaire getInstruments() pour récupérer les instruments
        return document.getInstruments().stream()
                .map(mapper::toDto)
                .collect(Collectors.toSet());
    }

    /**
     * Supprime un instrument d’un document
     */
    public void removeInstrumentFromDocument(Long documentId, Long instrumentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document non trouvé : " + documentId));

        Instrument instrument = instrumentRepository.findById(instrumentId)
                .orElseThrow(() -> new RuntimeException("Instrument non trouvé : " + instrumentId));

        document.removeInstrument(instrument);

        documentRepository.save(document);
    }
}
