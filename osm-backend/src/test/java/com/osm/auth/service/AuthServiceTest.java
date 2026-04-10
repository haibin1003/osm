package com.osm.auth.service;

import com.osm.auth.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    @Test
    void shouldValidateToken() {
        String validToken = "valid.jwt.token";
        String invalidToken = "invalid.token";

        when(tokenProvider.validateToken(validToken)).thenReturn(true);
        when(tokenProvider.validateToken(invalidToken)).thenReturn(false);

        assertTrue(tokenProvider.validateToken(validToken));
        assertFalse(tokenProvider.validateToken(invalidToken));
    }

    @Test
    void shouldGenerateToken() {
        String username = "admin";
        String expectedToken = "test-jwt-token";

        when(tokenProvider.generateToken(any())).thenReturn(expectedToken);

        String token = tokenProvider.generateToken(null);
        assertEquals(expectedToken, token);
    }

    @Test
    void shouldGetUsernameFromToken() {
        String token = "some.jwt.token";
        String username = "admin";

        when(tokenProvider.getUsernameFromToken(token)).thenReturn(username);

        String result = tokenProvider.getUsernameFromToken(token);
        assertEquals(username, result);
    }
}
