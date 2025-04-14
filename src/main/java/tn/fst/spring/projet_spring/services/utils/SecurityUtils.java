package tn.fst.spring.projet_spring.services.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import tn.fst.spring.projet_spring.model.auth.User;
import tn.fst.spring.projet_spring.repositories.auth.UserRepository;

public class SecurityUtils {
    public static User getCurrentUser(UserRepository userRepository) {
        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        String username=auth.getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

    }
}
