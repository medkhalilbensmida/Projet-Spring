package tn.fst.spring.projet_spring.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import tn.fst.spring.projet_spring.model.auth.User;
import tn.fst.spring.projet_spring.model.order.Order;
import tn.fst.spring.projet_spring.repositories.auth.UserRepository;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final UserRepository userRepository;

    /**
     * Checks if the current user is an admin
     * @return true if the user has ROLE_ADMIN, false otherwise
     */
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
                authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    /**
     * Gets the current authenticated user
     * @return the User entity
     * @throws ResponseStatusException if no user is authenticated
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    /**
     * Checks if the current user owns the given order or is an admin
     * @param order the order to check ownership for
     * @throws ResponseStatusException if user doesn't own the order and is not an admin
     */
    public void checkOrderOwnership(Order order) {
        if (isAdmin()) {
            return; // Admins can access any order
        }

        User currentUser = getCurrentUser();
        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to access this order"
            );
        }
    }
}