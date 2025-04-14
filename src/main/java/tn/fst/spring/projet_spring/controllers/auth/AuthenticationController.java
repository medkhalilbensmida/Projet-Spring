package tn.fst.spring.projet_spring.controllers.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.projet_spring.dto.auth.*;
import tn.fst.spring.projet_spring.model.auth.Role;
import tn.fst.spring.projet_spring.model.auth.User;
import tn.fst.spring.projet_spring.repositories.auth.RoleRepository;
import tn.fst.spring.projet_spring.repositories.auth.UserRepository;
import tn.fst.spring.projet_spring.security.jwt.JwtService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails user = (UserDetails) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken));
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@RequestBody @Valid RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Email déjà utilisé"));
        }

        Role role = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Rôle par défaut non trouvé"));

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                role
        );

        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("Utilisateur enregistré avec succès"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Refresh token manquant"));
        }

        String username = jwtService.extractUsername(refreshToken);

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), List.of()
        );

        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            return ResponseEntity.status(401).body(new MessageResponse("Refresh token invalide"));
        }

        String newAccessToken = jwtService.generateAccessToken(userDetails);
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

}
