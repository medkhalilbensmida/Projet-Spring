package tn.fst.spring.projet_spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ProjetSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjetSpringApplication.class, args);
    }

}
