package com.talenthub.security;

import com.talenthub.entity.Role;
import com.talenthub.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;
    private final String secret = "9a676786a7d51944d1e2e4b6b669fcf78c2e6cd90ad27e85c18e11a1a7c5cde78a1e2f3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8";
    private final int expirationMs = 86400000; // 24 hours

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider(secret, expirationMs);
    }

    @Test
    void testGenerateAndValidateToken() {
        // Arrange
        User user = User.builder()
                .id(42L)
                .email("artist@talenthub.com")
                .password("hashed_password")
                .role(Role.ARTIST)
                .build();

        UserPrincipal principal = UserPrincipal.create(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        // Act
        String token = tokenProvider.generateToken(auth);
        
        // Assert
        assertNotNull(token);
        assertTrue(tokenProvider.validateToken(token));
        assertEquals(42L, tokenProvider.getUserIdFromJWT(token));
    }

    @Test
    void testInvalidToken() {
        // Act & Assert
        assertFalse(tokenProvider.validateToken("invalid-token-string"));
    }
}
