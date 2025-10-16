package fr.afpa.choral_riff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing; 

@SpringBootApplication
@EnableJpaAuditing
public class ChoralRiffApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChoralRiffApplication.class, args);
    }

}