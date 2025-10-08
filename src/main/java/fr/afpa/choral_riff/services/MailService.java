package fr.afpa.choral_riff.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendInvitationEmail(String to, String token) {
        String link = "https://ton-frontend/accept-invitation?token=" + token;
        String subject = "Invitation à rejoindre un ensemble";
        String text = "Bonjour,\n\nVous avez été invité à rejoindre un ensemble musical. " +
                "Pour accepter l'invitation, cliquez sur le lien suivant :\n" + link +
                "\n\nMerci.";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("98aa7e002@smtp-brevo.com"); // Assure-toi que c'est bien celui dans application.properties

        mailSender.send(message);
    }
}
