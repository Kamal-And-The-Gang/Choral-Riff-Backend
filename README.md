# Chorall-Riff-Backend


## Description
Ce projet backend gère les ensembles musicaux pour l'application Choral Riff.  
Il expose des API REST pour créer, lire, mettre à jour et supprimer des ensembles.

## Technologies
- Java 17
- Spring Boot 3
- Spring Data JPA
- Hibernate
- H2 / MySQL (configurable dans `application.properties`)
- PostgreSQL
- Maven

### Configuration

Les informations sensibles (mot de passe de base de données, clé JWT, identifiants SMTP, etc.) ne sont pas versionnées dans Git.

Elles sont fournies via les variables d'environnement ou le profil Spring Boot `dev`.

Exemple :

```properties
spring.datasource.password=${spring.datasource.password}
security.jwt.secret-key=${security.jwt.secret-key}
spring.mail.username=${spring.mail.username}
spring.mail.password=${spring.mail.password}
```

Un fichier local `application-dev.properties` peut être utilisé pour renseigner ces valeurs lors du développement. Ce fichier n'est pas versionné dans Git.
