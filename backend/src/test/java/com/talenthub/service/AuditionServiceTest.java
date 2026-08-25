package com.talenthub.service;

import com.talenthub.dto.AuditionRequestDto;
import com.talenthub.dto.AuditionResponseDto;
import com.talenthub.entity.Audition;
import com.talenthub.entity.OrganizerProfile;
import com.talenthub.entity.Role;
import com.talenthub.entity.User;
import com.talenthub.repository.AuditionRepository;
import com.talenthub.repository.OrganizerProfileRepository;
import com.talenthub.service.impl.AuditionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditionServiceTest {

    @Mock
    private AuditionRepository auditionRepository;

    @Mock
    private OrganizerProfileRepository organizerProfileRepository;

    @InjectMocks
    private AuditionServiceImpl auditionService;

    @Test
    void testCreateAuditionSuccess() {
        // Arrange
        User user = User.builder().id(2L).email("org@test.com").role(Role.ORGANIZER).build();
        OrganizerProfile organizer = OrganizerProfile.builder().id(20L).user(user).organizationName("Star Agency").build();
        AuditionRequestDto dto = AuditionRequestDto.builder()
                .title("Lead Actor")
                .description("Looking for male lead")
                .category("Acting")
                .location("NY")
                .applicationDeadline(LocalDate.now().plusDays(10))
                .build();

        when(organizerProfileRepository.findByUserId(2L)).thenReturn(Optional.of(organizer));
        when(auditionRepository.save(any(Audition.class))).thenAnswer(i -> {
            Audition aud = (Audition) i.getArguments()[0];
            aud.setId(100L);
            return aud;
        });

        // Act
        AuditionResponseDto result = auditionService.createAudition(2L, dto);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("Lead Actor", result.getTitle());
        assertEquals("Star Agency", result.getOrganizationName());
    }

    @Test
    void testCreateAuditionInvalidCategoryThrowsException() {
        // Arrange
        AuditionRequestDto dto = AuditionRequestDto.builder()
                .title("Painter")
                .description("Looking for painter")
                .category("Painting") // Invalid category
                .build();

        // Act & Assert
        assertThrows(RuntimeException.class, () -> auditionService.createAudition(2L, dto));
    }

    @Test
    void testUpdateAuditionSuccess() {
        // Arrange
        User user = User.builder().id(2L).email("org@test.com").role(Role.ORGANIZER).build();
        OrganizerProfile organizer = OrganizerProfile.builder().id(20L).user(user).build();
        Audition audition = Audition.builder()
                .id(100L)
                .organizer(organizer)
                .title("Old Title")
                .category("Acting")
                .build();

        AuditionRequestDto dto = AuditionRequestDto.builder()
                .title("New Title")
                .description("Updated description")
                .category("Singing")
                .location("LA")
                .applicationDeadline(LocalDate.now().plusDays(5))
                .build();

        when(auditionRepository.findById(100L)).thenReturn(Optional.of(audition));
        when(auditionRepository.save(any(Audition.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        AuditionResponseDto result = auditionService.updateAudition(2L, 100L, dto);

        // Assert
        assertEquals("New Title", result.getTitle());
        assertEquals("Singing", result.getCategory());
    }

    @Test
    void testUpdateAuditionUnauthorizedThrowsException() {
        // Arrange
        User owner = User.builder().id(2L).role(Role.ORGANIZER).build();
        OrganizerProfile organizer = OrganizerProfile.builder().id(20L).user(owner).build();
        Audition audition = Audition.builder()
                .id(100L)
                .organizer(organizer)
                .title("Owner Title")
                .category("Acting")
                .build();

        AuditionRequestDto dto = AuditionRequestDto.builder()
                .title("Hacker Title")
                .category("Acting")
                .build();

        when(auditionRepository.findById(100L)).thenReturn(Optional.of(audition));

        // Act & Assert (Attempting to update using userId 3, who is not the owner)
        assertThrows(RuntimeException.class, () -> auditionService.updateAudition(3L, 100L, dto));
    }

    @Test
    void testDeleteAuditionUnauthorizedThrowsException() {
        // Arrange
        User owner = User.builder().id(2L).role(Role.ORGANIZER).build();
        OrganizerProfile organizer = OrganizerProfile.builder().id(20L).user(owner).build();
        Audition audition = Audition.builder()
                .id(100L)
                .organizer(organizer)
                .build();

        when(auditionRepository.findById(100L)).thenReturn(Optional.of(audition));

        // Act & Assert (Attempting to delete using userId 3)
        assertThrows(RuntimeException.class, () -> auditionService.deleteAudition(3L, 100L));
    }

    @Test
    void testGetActiveAuditions() {
        // Arrange
        User user = User.builder().id(2L).role(Role.ORGANIZER).build();
        OrganizerProfile organizer = OrganizerProfile.builder().id(20L).user(user).organizationName("Star Agency").build();
        Audition activeAudition = Audition.builder()
                .id(100L)
                .organizer(organizer)
                .title("Active Post")
                .category("Dancing")
                .status("ACTIVE")
                .build();

        when(auditionRepository.findByStatus("ACTIVE")).thenReturn(Collections.singletonList(activeAudition));

        // Act
        List<AuditionResponseDto> results = auditionService.getActiveAuditions();

        // Assert
        assertEquals(1, results.size());
        assertEquals("Active Post", results.get(0).getTitle());
    }
}
