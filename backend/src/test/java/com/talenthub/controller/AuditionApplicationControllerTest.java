package com.talenthub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talenthub.dto.ApiResponse;
import com.talenthub.dto.ApplicationRequestDto;
import com.talenthub.dto.ArtistApplicationResponseDto;
import com.talenthub.dto.StatusUpdateRequestDto;
import com.talenthub.entity.Role;
import com.talenthub.entity.User;
import com.talenthub.security.CustomUserDetailsService;
import com.talenthub.security.JwtTokenProvider;
import com.talenthub.security.UserPrincipal;
import com.talenthub.service.AuditionApplicationService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuditionApplicationController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class AuditionApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditionApplicationService applicationService;

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
    void testApplyToAuditionAsArtistSuccess() throws Exception {
        authenticateAs(Role.ARTIST, 1L, "artist@talenthub.com");
        ApplicationRequestDto request = new ApplicationRequestDto("Message text");
        ArtistApplicationResponseDto response = ArtistApplicationResponseDto.builder().id(100L).build();

        when(applicationService.applyToAudition(eq(1L), eq(10L), any(ApplicationRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/auditions/10/applications")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void testApplyToAuditionAsOrganizerForbidden() throws Exception {
        // Req 8: Organizer cannot apply to an audition
        authenticateAs(Role.ORGANIZER, 2L, "org@talenthub.com");
        ApplicationRequestDto request = new ApplicationRequestDto("Message text");

        mockMvc.perform(post("/auditions/10/applications")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden()); // Expected 403 Forbidden for ORGANIZER role
    }

    @Test
    void testUpdateApplicationStatusAsOrganizerSuccess() throws Exception {
        // Req 6: Organizer can change application status
        authenticateAs(Role.ORGANIZER, 2L, "org@talenthub.com");
        StatusUpdateRequestDto request = new StatusUpdateRequestDto("SHORTLISTED");

        doNothing().when(applicationService).updateApplicationStatus(eq(2L), eq(500L), eq("SHORTLISTED"));

        mockMvc.perform(put("/applications/500/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateApplicationStatusAsArtistForbidden() throws Exception {
        // Req 7: Artist cannot change application status
        authenticateAs(Role.ARTIST, 1L, "artist@talenthub.com");
        StatusUpdateRequestDto request = new StatusUpdateRequestDto("SHORTLISTED");

        mockMvc.perform(put("/applications/500/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden()); // Expected 403 Forbidden for ARTIST role
    }
}
