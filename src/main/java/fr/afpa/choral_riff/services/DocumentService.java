package main.java.fr.afpa.choral_riff.services;
import fr.afpa.choral_riff.entity.Document;
import fr.afpa.choral_riff.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
 


@Service
public class DocumentService {

    private DocumentRepository documentRepository;

    // Injection via constructeur
    public DocumentService(DocumentRepository documentRepository) {
        this.adminRepository = adminRepository;
    }

    public Optional<Admin> findById(Integer id) {
        return adminRepository.findById(id);
    }

    public Admin save(Admin admin) {
        return adminRepository.save(admin);
    }

    // Login : vérifie si un admin existe avec cet email + mot de passe
    // public Optional<Admin> login(String email, String motDePasse) {
    //     return adminRepository.findByEmailAndMotDePasse(email, motDePasse);
    // }

}
