package com.talenthub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talenthub.dto.AuditionRequestDto;
import com.talenthub.dto.AuditionResponseDto;
import com.talenthub.entity.Role;
import com.talenthub.entity.User;
import com.talenthub.security.CustomUserDetailsService;
import com.talenthub.security.JwtTokenProvider;
import com.talenthub.security.UserPrincipal;
import com.talenthub.service.AuditionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import com.talenthub.config.SecurityConfig;
import com.talenthub.security.JwtAuthenticationFilter;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuditionController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class AuditionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditionService auditionService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateAs(Role role, Long id, String email) {
        User user = User.builder()
                .id(id)
                .email(email)
                .role(role)
                .build();
        UserPrincipal principal = UserPrincipal.create(user);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void testCreateAuditionAsOrganizerSuccess() throws Exception {
        authenticateAs(Role.ORGANIZER, 2L, "org@talenthub.com");

        AuditionRequestDto request = AuditionRequestDto.builder()
                .title("Lead Actor")
                .description("Desc")
                .category("Acting")
                .location("Chicago")
                .applicationDeadline(LocalDate.now().plusDays(10))
                .build();

        AuditionResponseDto response = AuditionResponseDto.builder()
                .id(1L)
                .title("Lead Actor")
                .build();

        when(auditionService.createAudition(any(), any(AuditionRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/auditions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void testCreateAuditionAsArtistForbidden() throws Exception {
        authenticateAs(Role.ARTIST, 1L, "artist@talenthub.com");

        AuditionRequestDto request = AuditionRequestDto.builder()
                .title("Lead Actor")
                .description("Desc")
                .category("Acting")
                .location("Chicago")
                .applicationDeadline(LocalDate.now().plusDays(10))
                .build();

        mockMvc.perform(post("/auditions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden()); // Expected 403 Forbidden for ARTIST role
    }

    @Test
    void testBrowseActiveAuditionsPublicAllowed() throws Exception {
        when(auditionService.getActiveAuditions()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/auditions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()); // Expected 200 OK for anonymous users
    }
}
