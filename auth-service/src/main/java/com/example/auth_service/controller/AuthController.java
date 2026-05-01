package com.example.auth_service.controller;


import com.example.auth_service.dto.*;
import com.example.auth_service.security.JwtUtils;
import com.example.auth_service.service.AuthService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;

    // PUBLIC
    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDTO> signup(@RequestBody SignUpRequestDTO request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    // PUBLIC
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // FEIGN ACCESS + OWNER CHECK
    @GetMapping("/{id}")
    @PostAuthorize("returnObject.body.id == authentication.principal.id OR hasAuthority('ADMIN')")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(authService.getUserById(id));
    }

    // ADMIN ONLY
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }

    // PUBLIC
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        authService.sendResetToken(email);
        return ResponseEntity.ok("Reset token sent");
    }

    // PUBLIC
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String email,
            @RequestParam String token,
            @RequestParam String newPassword) {

        return ResponseEntity.ok(
                authService.resetPassword(email, token, newPassword)
        );
    }

    // FEIGN / GATEWAY
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestParam String token) {
        return ResponseEntity.ok(jwtUtils.isTokenValid(token));
    }

    // DISABLE USER (SOFT DELETE)
    @PutMapping("/disable/{id}")
    @PostAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserDto> disableUser(@PathVariable Long id) {
        return ResponseEntity.ok(authService.disableUser(id));
    }
}