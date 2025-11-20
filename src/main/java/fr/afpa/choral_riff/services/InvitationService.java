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

import java.time.LocalDate;
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

    public InvitationService(
            InvitationRepository invitationRepository,
            InvitationMapper invitationMapper,
            EnsembleRepository ensembleRepository,
            UtilisateurRepository utilisateurRepository,
            MailService mailService,
            CreateInvitationMapper createInvitationMapper,
            UtilisateurEnsembleRepository utilisateurEnsembleRepository) {
        this.invitationRepository = invitationRepository;
        this.invitationMapper = invitationMapper;
        this.ensembleRepository = ensembleRepository;
        this.mailService = mailService;
        this.createInvitationMapper = createInvitationMapper;
        this.utilisateurEnsembleRepository = utilisateurEnsembleRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    // === Récupérer toutes les invitations pour un ensemble ===
    public List<InvitationDTO> getAllByEnsembleId(Long ensembleId) {
        return invitationRepository.findByEnsembleId(ensembleId).stream()
                .map(invitationMapper::toDto)
                .collect(Collectors.toList());
    }

    public InvitationDTO creerInvitation(CreateInvitationDTO dto) {
        // Vérifie si une invitation existe déjà pour cet email et ensemble
        if (invitationRepository.existsByEmailInviteAndEnsembleId(dto.getEmailInvite(), dto.getEnsembleId())) {
            throw new IllegalArgumentException("Une invitation existe déjà pour cet email.");
        }

        // Récupère l'ensemble
        Ensemble ensemble = ensembleRepository.findById(dto.getEnsembleId())
                .orElseThrow(() -> new EntityNotFoundException("Ensemble introuvable"));

        // Crée l'invitation simple
        Invitation invitation = new Invitation();
        invitation.setEmailInvite(dto.getEmailInvite());
        invitation.setEnsemble(ensemble);

        // Générer et assigner un token
        String token = UUID.randomUUID().toString();
        invitation.setToken(token);

        invitation.setDateEnvoi(LocalDate.now());
        invitation.setDateExpiration(LocalDate.now().plusDays(7)); // Expiration dans 7 jours

        // Sauvegarde
        invitationRepository.save(invitation);

        // Envoyer le mail avec le token
        // mailService.sendInvitationEmail(invitation.getEmailInvite(), null);
        mailService.sendInvitationEmail(invitation.getEmailInvite(), invitation.getToken());

        // Convertit et renvoie le DTO
        return invitationMapper.toDto(invitation);
    }

    public InvitationDTO createSimple(CreateInvitationDTO dto) {
        // Vérifie s'il existe déjà une invitation pour cet email
        if (invitationRepository.existsByEmailInvite(dto.getEmailInvite())) {
            throw new IllegalArgumentException("Une invitation existe déjà pour cet email.");
        }

        // Cherche l'ensemble par ID
        Ensemble ensemble = ensembleRepository.findById(dto.getEnsembleId())
                .orElseThrow(() -> new RuntimeException("Ensemble non trouvé"));

        // Crée une nouvelle invitation à partir du DTO
        Invitation invitation = createInvitationMapper.toEntity(dto);

        // Associe l'ensemble à l'invitation
        invitation.setEnsemble(ensemble);

        // Sauvegarde l'invitation en base
        Invitation saved = invitationRepository.save(invitation);

        // Retourne le DTO résultat
        return invitationMapper.toDto(saved);
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

    /**
     * Rattache un utilisateur à une invitation après son inscription via le token.
     * 
     * @param token             Le token unique de l'invitation
     * @param nouvelUtilisateur L'utilisateur créé (inscrit)
     * @throws RuntimeException si l'invitation n'existe pas ou est déjà rattachée
     */

    @Transactional
    public void rattacherUtilisateurApresInscription(String token, Utilisateur nouvelUtilisateur) {
        // 1 Récupérer l'invitation
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invitation non trouvée avec ce token."));

        if (invitation.getUtilisateur() != null) {
            throw new RuntimeException("Cette invitation est déjà rattachée à un utilisateur.");
        }

        // 2 Vérifier si un utilisateur existe déjà avec cet email
        Optional<Utilisateur> optUser = utilisateurRepository.findByEmail(invitation.getEmailInvite());

        if (optUser.isPresent()) {
            Utilisateur user = optUser.get(); // on récupère l'utilisateur réel

            // L'utilisateur existe déjà → ne pas créer un nouveau compte
            // Juste l’attacher à l’ensemble
            invitation.setUtilisateur(user);
            invitation.setEtat(StatusInvitation.ACCEPTEE);
            invitationRepository.saveAndFlush(invitation);

            UtilisateurEnsemble ue = new UtilisateurEnsemble();
            ue.setUtilisateur(user);
            ue.setEnsemble(invitation.getEnsemble());
            ue.setRoleDansEnsemble(Role.MEMBRE);
            ue.setDateAdhesion(LocalDate.now());
            utilisateurEnsembleRepository.saveAndFlush(ue);

            return;
        }

        // 3 Sinon, utiliser le nouvel utilisateur créé à l'inscription
        invitation.setUtilisateur(nouvelUtilisateur);
        invitation.setEtat(StatusInvitation.ACCEPTEE);
        invitationRepository.saveAndFlush(invitation);

        UtilisateurEnsemble ue = new UtilisateurEnsemble();
        ue.setUtilisateur(nouvelUtilisateur);
        ue.setEnsemble(invitation.getEnsemble());
        ue.setRoleDansEnsemble(Role.MEMBRE);
        ue.setDateAdhesion(LocalDate.now());
        utilisateurEnsembleRepository.saveAndFlush(ue);
    }

    // InvitationService.java

    // public Invitation getByTokenEntity(String token) {
    // return invitationRepository.findByToken(token)
    // .orElseThrow(() -> new RuntimeException("Invitation non trouvée avec ce
    // token."));
    // }
    public Invitation getByTokenEntity(String token) {
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invitation non trouvée avec ce token."));

        // Vérifier expiration
        if (invitation.getDateExpiration() != null &&
                invitation.getDateExpiration().isBefore(LocalDate.now())) {
            throw new RuntimeException("Le lien d'invitation a expiré.");
        }

        return invitation;
    }

}
