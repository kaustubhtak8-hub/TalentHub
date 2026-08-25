package com.talenthub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talenthub.dto.AuthRequest;
import com.talenthub.dto.AuthResponse;
import com.talenthub.dto.RegisterRequest;
import com.talenthub.entity.Role;
import com.talenthub.entity.User;
import com.talenthub.security.CustomUserDetailsService;
import com.talenthub.security.JwtTokenProvider;
import com.talenthub.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import com.talenthub.config.SecurityConfig;
import com.talenthub.security.JwtAuthenticationFilter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegisterUser() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@talenthub.com");
        request.setPassword("password123");
        request.setRole(Role.ARTIST);

        User mockUser = User.builder()
                .id(1L)
                .email(request.getEmail())
                .role(request.getRole())
                .build();

        when(authService.registerUser(any(RegisterRequest.class))).thenReturn(mockUser);

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    @Test
    void testLoginUser() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setEmail("test@talenthub.com");
        request.setPassword("password123");

        AuthResponse mockResponse = new AuthResponse("mock-jwt-token", request.getEmail(), "ARTIST");

        when(authService.authenticateUser(any(AuthRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.email").value("test@talenthub.com"))
                .andExpect(jsonPath("$.role").value("ARTIST"));
    }
}
