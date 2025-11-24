// package fr.afpa.choral_riff.services;

// import fr.afpa.choral_riff.dto.CreateInvitationDTO;
// import fr.afpa.choral_riff.dto.InvitationDTO;
// import fr.afpa.choral_riff.entity.*;
// import fr.afpa.choral_riff.mapper.CreateInvitationMapper;
// import fr.afpa.choral_riff.mapper.InvitationMapper;
// import fr.afpa.choral_riff.repositories.EnsembleRepository;
// import fr.afpa.choral_riff.repositories.InvitationRepository;
// import fr.afpa.choral_riff.repositories.UtilisateurEnsembleRepository;
// import fr.afpa.choral_riff.repositories.UtilisateurRepository;

// import jakarta.persistence.EntityNotFoundException;
// import jakarta.transaction.Transactional;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.stereotype.Service;

// import java.time.LocalDate;
// import java.util.List;
// import java.util.Optional;
// import java.util.UUID;
// import java.util.stream.Collectors;

// @Service
// public class InvitationService {

//     private final InvitationRepository invitationRepository;
//     private final InvitationMapper invitationMapper;
//     private final EnsembleRepository ensembleRepository;
//     private final UtilisateurRepository utilisateurRepository;
//     private final MailService mailService;
//     private final CreateInvitationMapper createInvitationMapper;
//     private final UtilisateurEnsembleRepository utilisateurEnsembleRepository;

//     private static final Logger logger = LoggerFactory.getLogger(InvitationService.class);

//     public InvitationService(
//             InvitationRepository invitationRepository,
//             InvitationMapper invitationMapper,
//             EnsembleRepository ensembleRepository,
//             UtilisateurRepository utilisateurRepository,
//             MailService mailService,
//             CreateInvitationMapper createInvitationMapper,
//             UtilisateurEnsembleRepository utilisateurEnsembleRepository) {

//         this.invitationRepository = invitationRepository;
//         this.invitationMapper = invitationMapper;
//         this.ensembleRepository = ensembleRepository;
//         this.utilisateurRepository = utilisateurRepository;
//         this.mailService = mailService;
//         this.createInvitationMapper = createInvitationMapper;
//         this.utilisateurEnsembleRepository = utilisateurEnsembleRepository;
//     }

//     // === Récupérer toutes les invitations pour un ensemble ===
//     public List<InvitationDTO> getAllByEnsembleId(Long ensembleId) {
//         return invitationRepository.findByEnsembleId(ensembleId).stream()
//                 .map(invitationMapper::toDto)
//                 .collect(Collectors.toList());
//     }

//     // === Créer une invitation simple ===
//     public InvitationDTO creerInvitation(CreateInvitationDTO dto) {
//         if (invitationRepository.existsByEmailInviteAndEnsembleId(dto.getEmailInvite(), dto.getEnsembleId())) {
//             throw new IllegalArgumentException("Une invitation existe déjà pour cet email.");
//         }

//         Ensemble ensemble = ensembleRepository.findById(dto.getEnsembleId())
//                 .orElseThrow(() -> new EntityNotFoundException("Ensemble introuvable"));

//         Invitation invitation = new Invitation();
//         invitation.setEmailInvite(dto.getEmailInvite());
//         invitation.setEnsemble(ensemble);

//         // Générer un token
//         invitation.setToken(UUID.randomUUID().toString());
//         invitation.setDateEnvoi(LocalDate.now());
//         invitation.setDateExpiration(LocalDate.now().plusDays(7));

//         invitationRepository.save(invitation);

//         // Envoyer l'email
//         mailService.sendInvitationEmail(invitation.getEmailInvite(), invitation.getToken());

//         return invitationMapper.toDto(invitation);
//     }

//     public InvitationDTO createSimple(CreateInvitationDTO dto) {
//         if (invitationRepository.existsByEmailInvite(dto.getEmailInvite())) {
//             throw new IllegalArgumentException("Une invitation existe déjà pour cet email.");
//         }

//         Ensemble ensemble = ensembleRepository.findById(dto.getEnsembleId())
//                 .orElseThrow(() -> new RuntimeException("Ensemble non trouvé"));

//         Invitation invitation = createInvitationMapper.toEntity(dto);
//         invitation.setEnsemble(ensemble);
//         Invitation saved = invitationRepository.save(invitation);

//         return invitationMapper.toDto(saved);
//     }

//     // === Accepter une invitation via token ===
//     public InvitationDTO accept(String token) {
//         Invitation invitation = invitationRepository.findByToken(token)
//                 .orElseThrow(() -> new RuntimeException("Invitation non trouvée avec ce token."));
//         invitation.setEtat(StatusInvitation.ACCEPTEE);
//         return invitationMapper.toDto(invitationRepository.save(invitation));
//     }

//     // === Refuser une invitation via token ===
//     public InvitationDTO refuse(String token) {
//         Invitation invitation = invitationRepository.findByToken(token)
//                 .orElseThrow(() -> new RuntimeException("Invitation non trouvée avec ce token."));
//         invitation.setEtat(StatusInvitation.REFUSEE);
//         return invitationMapper.toDto(invitationRepository.save(invitation));
//     }

//     // === Supprimer une invitation ===
//     public void delete(Long id) {
//         if (!invitationRepository.existsById(id)) {
//             throw new RuntimeException("Invitation non trouvée avec l'ID : " + id);
//         }
//         invitationRepository.deleteById(id);
//     }

//     // === Récupérer une invitation par token ===
//     public InvitationDTO getByToken(String token) {
//         Invitation invitation = invitationRepository.findByToken(token)
//                 .orElseThrow(() -> new RuntimeException("Invitation non trouvée avec ce token."));
//         return invitationMapper.toDto(invitation);
//     }

//     public Invitation getByTokenEntity(String token) {
//         Invitation invitation = invitationRepository.findByToken(token)
//                 .orElseThrow(() -> new RuntimeException("Invitation non trouvée avec ce token."));

//         if (invitation.getDateExpiration() != null && invitation.getDateExpiration().isBefore(LocalDate.now())) {
//             throw new RuntimeException("Le lien d'invitation a expiré.");
//         }
//         return invitation;
//     }

//     // === Rattacher un utilisateur après inscription via token ===
//     @Transactional
//     public void rattacherUtilisateurApresInscription(String token, Utilisateur nouvelUtilisateur) {
//         Invitation invitation = invitationRepository.findByToken(token)
//                 .orElseThrow(() -> new RuntimeException("Invitation non trouvée avec ce token."));

//         if (invitation.getUtilisateur() != null) {
//             throw new RuntimeException("Cette invitation est déjà rattachée à un utilisateur.");
//         }

//         Optional<Utilisateur> optUser = utilisateurRepository.findByEmail(invitation.getEmailInvite());

//         Utilisateur userToAttach = optUser.orElse(nouvelUtilisateur);

//         invitation.setUtilisateur(userToAttach);
//         invitation.setEtat(StatusInvitation.ACCEPTEE);
//         invitationRepository.saveAndFlush(invitation);

//         UtilisateurEnsemble ue = new UtilisateurEnsemble();
//         ue.setUtilisateur(userToAttach);
//         ue.setEnsemble(invitation.getEnsemble());
//         ue.setRoleDansEnsemble(Role.MEMBRE);
//         ue.setDateAdhesion(LocalDate.now());
//         utilisateurEnsembleRepository.saveAndFlush(ue);
//     }
// }
