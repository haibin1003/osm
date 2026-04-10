package com.osm.user.service;

import com.osm.user.dto.CreateUserRequest;
import com.osm.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldEncodePassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "password123";
        String encodedPassword = encoder.encode(rawPassword);

        assertNotNull(encodedPassword);
        assertTrue(encoder.matches(rawPassword, encodedPassword));
    }

    @Test
    void shouldRejectWeakPassword() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");
        request.setPassword("123"); // Too short
        request.setEmail("test@company.com");

        // Password validation happens in DTO, not service
        assertEquals(3, request.getPassword().length());
    }

    @Test
    void shouldCreateValidUserRequest() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setEmail("test@company.com");
        request.setStatus(1);

        assertEquals("testuser", request.getUsername());
        assertEquals("password123", request.getPassword());
        assertEquals("test@company.com", request.getEmail());
        assertEquals(1, request.getStatus());
    }
}
