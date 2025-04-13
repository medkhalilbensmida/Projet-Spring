package tn.fst.spring.projet_spring.controllers.auth;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.projet_spring.dto.auth.UserDto;
import tn.fst.spring.projet_spring.dto.auth.UserAdminUpdateDto;
import tn.fst.spring.projet_spring.dto.auth.UpdateUserRoleRequest;
import tn.fst.spring.projet_spring.dto.auth.UserProfileUpdateDto;
import tn.fst.spring.projet_spring.services.interfaces.IUserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @Operation(summary = "Lister tous les utilisateurs (admin seulement)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Supprimer un utilisateur par son ID (admin seulement)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Récupérer les informations de l'utilisateur connecté")
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(userService.getUserByEmail(authentication.getName()));
    }

    @Operation(summary = "Mettre à jour son propre profil (email, mot de passe, username)")
    @PutMapping("/me")
    public ResponseEntity<UserDto> updateCurrentUser(@RequestBody UserProfileUpdateDto profileDto, Authentication authentication) {
        return ResponseEntity.ok(userService.updateCurrentUserProfile(authentication.getName(), profileDto));
    }

    @Operation(summary = "Mettre à jour les données d'un utilisateur (admin seulement, hors rôle)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateUserByAdmin(@PathVariable Long id, @RequestBody UserAdminUpdateDto adminDto) {
        return ResponseEntity.ok(userService.updateUserByAdmin(id, adminDto));
    }

    @Operation(summary = "Mettre à jour le rôle d'un utilisateur (admin seulement)")
    @PutMapping("/update-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateUserRole(@RequestBody UpdateUserRoleRequest request) {
        userService.updateUserRole(request.getUserId(), request.getRoleName());
        return ResponseEntity.ok("Rôle mis à jour avec succès");
    }
}
