package tn.fst.spring.projet_spring.repositories.auth;

import tn.fst.spring.projet_spring.entities.auth.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    boolean existsByName(String name);
}