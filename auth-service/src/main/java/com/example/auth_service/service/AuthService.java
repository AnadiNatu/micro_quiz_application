package com.example.auth_service.service;

import com.example.auth_service.dto.*;
import com.example.auth_service.entity.Users;
import com.example.auth_service.enums.UserRoles;
import com.example.auth_service.mapper.UserMapper;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.security.JwtUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    // ================= ADMIN INIT =================
    @PostConstruct
    public void createAdminAccount() {

        if (userRepository.findByUsernameIgnoreCase("admin").isEmpty()) {

            Users admin = new Users();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(UserRoles.ADMIN);
            admin.setEnabled(true);

            userRepository.save(admin);

            System.out.println("✅ Admin created");
        } else {
            System.out.println("⚠️ Admin already exists");
        }
    }

    // ================= SIGNUP =================
    public SignUpResponseDTO signup(SignUpRequestDTO request) {

        if (userRepository.findByUsernameIgnoreCase(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        Users user = UserMapper.toEntity(request);

        // 🔐 Encode password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Default role
        user.setRoles(UserRoles.PARTICIPANT);

        Users savedUser = userRepository.save(user);

        return UserMapper.toSignUpResponse(savedUser);
    }

    // ================= LOGIN =================
    public LoginResponseDTO login(LoginRequestDTO request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        Users user = userRepository.findByUsernameIgnoreCase(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtUtils.generateToken(user);

        return UserMapper.toLoginResponse(user, token);
    }

    // ================= GET USER =================
    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toDTO)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDTO)
                .toList();
    }

    // ================= RESET PASSWORD =================
    public void sendResetToken(String email) {

        Users user = userRepository.findByUsernameIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtUtils.generateToken(user);

        user.setResetToken(token);
        userRepository.save(user);

        // 👉 Hook your email service here
        System.out.println("Reset Token: " + token);
    }

    public String resetPassword(String email, String token, String newPassword) {

        Users user = userRepository.findByUsernameIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!jwtUtils.isTokenValid(token, user)) {
            throw new RuntimeException("Invalid or expired token");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);

        userRepository.save(user);

        return "Password reset successful";
    }

    // ================= RESET PASSWORD =================
    public UserDto disableUser(Long id) {

        Users user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEnabled(false);

        Users saved = userRepository.save(user);

        return UserMapper.toDTO(saved);
    }
}
