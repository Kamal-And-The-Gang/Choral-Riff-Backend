package fr.afpa.choral_riff.services;

import fr.afpa.choral_riff.dto.InvitationDTO;
import fr.afpa.choral_riff.entity.Ensemble;
import fr.afpa.choral_riff.entity.Invitation;
import fr.afpa.choral_riff.entity.StatutInvitation;
import fr.afpa.choral_riff.entity.Utilisateur;
import fr.afpa.choral_riff.mapper.InvitationMapper;
import fr.afpa.choral_riff.repositories.EnsembleRepository;
import fr.afpa.choral_riff.repositories.InvitationRepository;
import fr.afpa.choral_riff.repositories.UtilisateurRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final InvitationMapper invitationMapper;
    private final EnsembleRepository ensembleRepository;
    private final UtilisateurRepository utilisateurRepository;

    public InvitationService(InvitationRepository invitationRepository,
            InvitationMapper invitationMapper,
            EnsembleRepository ensembleRepository,
            UtilisateurRepository utilisateurRepository) {
        this.invitationRepository = invitationRepository;
        this.invitationMapper = invitationMapper;
        this.ensembleRepository = ensembleRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    // === Récupérer toutes les invitations pour un ensemble ===
    public List<InvitationDTO> getAllByEnsembleId(Long ensembleId) {
        return invitationRepository.findByEnsembleId(ensembleId).stream()
                .map(invitationMapper::toDto)
                .collect(Collectors.toList());
    }

    // === Créer une invitation ===
    public InvitationDTO create(InvitationDTO dto) {
        // Chercher l'utilisateur par email
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByEmail(dto.getEmailInvite());

        // Chercher l'ensemble par id
        Ensemble ensemble = ensembleRepository.findById(dto.getEnsembleId())
                .orElseThrow(() -> new RuntimeException("Ensemble non trouvé"));

        Invitation invitation = new Invitation();
        invitation.setEmailInvite(dto.getEmailInvite());
        invitation.setEnsemble(ensemble);

        // Si utilisateur trouvé, on le rattache, sinon on laisse null
        utilisateurOpt.ifPresent(invitation::setUtilisateur);

        // Initialiser le statut et autres champs
        invitation.setEtat(StatutInvitation.EN_ATTENTE);
        invitation.setToken(UUID.randomUUID().toString());

        // Sauvegarder l'invitation
        Invitation saved = invitationRepository.save(invitation);

        return invitationMapper.toDto(saved);
    }

    // === Accepter une invitation via token ===
    public InvitationDTO accept(String token) {
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invitation non trouvée avec ce token."));

        invitation.setEtat(StatutInvitation.ACCEPTEE);
        return invitationMapper.toDto(invitationRepository.save(invitation));
    }

    // === Refuser une invitation via token ===
    public InvitationDTO refuse(String token) {
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invitation non trouvée avec ce token."));

        invitation.setEtat(StatutInvitation.REFUSEE);
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
    public void rattacherUtilisateurAprèsInscription(String token, Utilisateur nouvelUtilisateur) {
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invitation non trouvée avec ce token."));

        if (invitation.getUtilisateur() != null) {
            throw new RuntimeException("Cette invitation est déjà rattachée à un utilisateur.");
        }

        invitation.setUtilisateur(nouvelUtilisateur);
        invitationRepository.save(invitation);
    }

}
