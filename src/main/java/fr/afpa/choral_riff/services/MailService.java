package fr.afpa.choral_riff.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendInvitationEmail(String to, String token) {
          // Vérification de l'adresse email
    if (to == null || to.isBlank()) {
        logger.warn("Impossible d'envoyer un email : adresse email vide ou nulle");
        return;
    }

        if (token == null || token.isEmpty()) {
            logger.warn("Impossible d'envoyer un email : token manquant pour l'adresse {}", to);
            return;
        }

        String link = "http://localhost:5173/Inscription?token=" + token;

        String subject = "Invitation à rejoindre un ensemble";
        String text = "Bonjour,\n\nVous avez été invité à rejoindre un ensemble musical. " +
                "Pour accepter l'invitation, cliquez sur le lien suivant :\n" + link +
                "\n\nMerci.";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text); // <-- utiliser le vrai contenu avec le lien
        message.setFrom("cchoralriff@gmail.com");
        mailSender.send(message);

        logger.info("Email envoyé à {}", to);
    }
}
