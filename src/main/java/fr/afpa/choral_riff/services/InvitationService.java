package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.dto.CreateInvitationDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import fr.afpa.choral_riff.repositories.UtilisateurEnsembleRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fr.afpa.choral_riff.dto.InvitationDTO;
import fr.afpa.choral_riff.entity.Ensemble;
import fr.afpa.choral_riff.entity.Invitation;
import fr.afpa.choral_riff.entity.Role;
import fr.afpa.choral_riff.entity.StatusInvitation;
import fr.afpa.choral_riff.entity.Utilisateur;
import fr.afpa.choral_riff.entity.UtilisateurEnsemble;
import fr.afpa.choral_riff.mapper.CreateInvitationMapper;
import fr.afpa.choral_riff.mapper.InvitationMapper;
import fr.afpa.choral_riff.repositories.EnsembleRepository;
import fr.afpa.choral_riff.repositories.InvitationRepository;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final InvitationMapper invitationMapper;
    private final EnsembleRepository ensembleRepository;
    private final MailService mailService;
    private final CreateInvitationMapper createInvitationMapper;
    private final UtilisateurEnsembleRepository utilisateurEnsembleRepository;
    private final UtilisateurRepository utilisateurRepository;

    private static final Logger logger = LoggerFactory.getLogger(InvitationService.class);

    private final NotificationService notificationService;

    public InvitationService(
            InvitationRepository invitationRepository,
            InvitationMapper invitationMapper,
            EnsembleRepository ensembleRepository,
            UtilisateurRepository utilisateurRepository,
            MailService mailService,
            CreateInvitationMapper createInvitationMapper,
            UtilisateurEnsembleRepository utilisateurEnsembleRepository,
            NotificationService notificationService) { // <-- ajouter ici
        this.invitationRepository = invitationRepository;
        this.invitationMapper = invitationMapper;
        this.ensembleRepository = ensembleRepository;
        this.mailService = mailService;
        this.createInvitationMapper = createInvitationMapper;
        this.utilisateurEnsembleRepository = utilisateurEnsembleRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.notificationService = notificationService; // <-- affecter
    }

    // === Récupérer toutes les invitations pour un ensemble ===
    public List<InvitationDTO> getAllByEnsembleId(Long ensembleId) {
        return invitationRepository.findByEnsembleId(ensembleId).stream()
                .map(invitationMapper::toDto)
                .collect(Collectors.toList());
    }

    public InvitationDTO creerInvitation(CreateInvitationDTO dto) {
        Optional<Utilisateur> optUser = utilisateurRepository.findByEmail(dto.getEmailInvite());
        Utilisateur utilisateur = optUser.orElse(null);

        if (utilisateur != null) {
            // Vérifier si déjà membre de cet ensemble
            boolean dejaMembre = utilisateurEnsembleRepository
                    .existsByUtilisateurIdAndEnsembleId(utilisateur.getId(), dto.getEnsembleId());

            InvitationDTO dtoResult = new InvitationDTO();
            dtoResult.setExistant(true);
            dtoResult.setUtilisateurId(utilisateur.getId());
            dtoResult.setDejaMembre(dejaMembre); // si déjà membre, inutile d’envoyer invitation
            return dtoResult;
        }

        // Sinon, créer l’invitation pour un nouvel utilisateur
        Ensemble ensemble = ensembleRepository.findById(dto.getEnsembleId())
                .orElseThrow(() -> new EntityNotFoundException("Ensemble introuvable"));

        Invitation invitation = new Invitation();
        invitation.setEmailInvite(dto.getEmailInvite());
        invitation.setEnsemble(ensemble);
        invitation.setToken(UUID.randomUUID().toString());
        invitation.setDateEnvoi(LocalDateTime.now());
        invitation.setDateExpiration(LocalDateTime.now().plusDays(7));
        invitation.setEtat(StatusInvitation.EN_ATTENTE); // ← IMPORTANT

        invitationRepository.save(invitation);
        mailService.sendInvitationEmail(invitation.getEmailInvite(), invitation.getToken());

        return invitationMapper.toDto(invitation);
    }

    // === Accepter une invitation via token ===
    public InvitationDTO accept(String token) {
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invitation non trouvée avec ce token."));

        invitation.setEtat(StatusInvitation.ACCEPTEE);
        return invitationMapper.toDto(invitationRepository.save(invitation));
    }

    // === Refuser une invitation via token ===
    public InvitationDTO refuse(String token) {
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invitation non trouvée avec ce token."));

        invitation.setEtat(StatusInvitation.REFUSEE);
        return invitationMapper.toDto(invitationRepository.save(invitation));
    }

    // === Supprimer une invitation ===
    public void delete(Long id) {
        if (!invitationRepository.existsById(id)) {
            throw new RuntimeException("Invitation non trouvée avec l'ID : " + id);
        }
        invitationRepository.deleteById(id);
    }

    // === Récupérer une invitation par token (utile pour lien dans l'email) ===
    public InvitationDTO getByToken(String token) {
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invitation non trouvée avec ce token."));
        return invitationMapper.toDto(invitation);
    }

    // Quelqu’un reçoit une invitation par email

    // Il s’inscrit (ou existe déjà)

    // On vérifie l’invitation

    // On ajoute l’utilisateur à un ensemble

    // On accepte l’invitation

    // On crée une notification

    // On renvoie un DTO au frontend

    @Transactional
    public InvitationDTO rattacherUtilisateurApresInscription(Utilisateur nouvelUtilisateur, Invitation invitation) {

        // Vérifier si l'utilisateur existe déjà
        Utilisateur utilisateurFinal = utilisateurRepository.findByEmail(invitation.getEmailInvite())
                .orElse(nouvelUtilisateur);

        // Vérifier si l'utilisateur est déjà membre de cet ensemble
        boolean dejaMembre = utilisateurEnsembleRepository
                .existsByUtilisateurIdAndEnsembleId(utilisateurFinal.getId(), invitation.getEnsemble().getId());

        InvitationDTO dto = invitationMapper.toDto(invitation);

        if (dejaMembre) {
            dto.setExistant(true);
            dto.setUtilisateurId(utilisateurFinal.getId());
            dto.setDejaMembre(true);
            return dto;
        }

        // Sauvegarder l'utilisateur si c'est un nouvel utilisateur
        if (utilisateurFinal.getId() == null) {
            utilisateurFinal = utilisateurRepository.save(utilisateurFinal);
        }

        // Rattacher l'utilisateur à l'invitation et accepter
        invitation.setUtilisateur(utilisateurFinal);
        invitation.setEtat(StatusInvitation.ACCEPTEE);
        invitationRepository.saveAndFlush(invitation);

       
        System.out.println("Invitation saved: id=" + invitation.getId());

        // Créer la notification
        notificationService.createNotification(
                utilisateurFinal.getId(),
                "INVITATION",
                "Vous avez été ajouté à l'ensemble " + invitation.getEnsemble().getNom(),
                invitation.getId() // <-- on passe juste l'ID pour rester compatible
        );

        // Ajouter l'utilisateur à l'ensemble
        UtilisateurEnsemble ue = new UtilisateurEnsemble();
        ue.setUtilisateur(utilisateurFinal);
        ue.setEnsemble(invitation.getEnsemble());
        ue.setRoleDansEnsemble(Role.MEMBRE);
        ue.setDateAdhesion(LocalDateTime.now());
        utilisateurEnsembleRepository.saveAndFlush(ue);

        // Retourner le DTO mis à jour
        return invitationMapper.toDto(invitation);
    }

    public Invitation getByTokenEntity(String token) {
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invitation non trouvée avec ce token."));

        // Vérifier expiration
        if (invitation.getDateExpiration() != null &&
                invitation.getDateExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Le lien d'invitation a expiré.");
        }

        return invitation;
    }

    // --- NOUVELLE METHODE POUR RECUPERER PAR ID ---
    public InvitationDTO getById(Long id) {
        Invitation invitation = invitationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invitation non trouvée"));

        InvitationDTO dto = new InvitationDTO();
        dto.setId(invitation.getId());
        dto.setEmailInvite(invitation.getEmailInvite());
        dto.setToken(invitation.getToken());
        dto.setUtilisateurId(invitation.getUtilisateur() != null ? invitation.getUtilisateur().getId() : null);
        dto.setUtilisateurNom(invitation.getUtilisateur() != null ? invitation.getUtilisateur().getNom() : null);
        dto.setEnsembleId(invitation.getEnsemble().getId());
        dto.setEnsembleNom(invitation.getEnsemble().getNom());
        dto.setEtat(invitation.getEtat() != null ? invitation.getEtat().name() : null);
        dto.setDateEnvoi(invitation.getDateEnvoi());
        dto.setExistant(invitation.getUtilisateur() != null);
        dto.setDejaMembre(invitation.getEtat() == StatusInvitation.ACCEPTEE);

        return dto;
    }

    

}
