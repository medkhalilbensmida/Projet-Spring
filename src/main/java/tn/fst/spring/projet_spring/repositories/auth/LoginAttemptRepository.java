package tn.fst.spring.projet_spring.repositories.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.fst.spring.projet_spring.model.auth.LoginAttempt;

import java.time.LocalDateTime;
import java.util.List;

public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {

    // Compter les tentatives échouées pour un utilisateur sur une période donnée
    long countByUserIdAndSuccessIsFalseAndTimestampAfter(Long userId, LocalDateTime since);

    // Compter toutes les tentatives échouées sur une période donnée
    long countBySuccessIsFalseAndTimestampAfter(LocalDateTime since);
}
