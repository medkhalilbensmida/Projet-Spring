package tn.fst.spring.projet_spring.repositories.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.fst.spring.projet_spring.entities.auth.Permission;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);
}