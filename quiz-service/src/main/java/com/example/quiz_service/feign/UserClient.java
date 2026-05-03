package com.example.quiz_service.feign;

import com.example.quiz_service.dto.applicationDTO.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service")
public interface UserClient {
    @GetMapping("/api/auth/internal/{id}") // The feign interface of endpoint for the user sync has been changed
    UserResponseDTO getUserById(@PathVariable Long id);
}
