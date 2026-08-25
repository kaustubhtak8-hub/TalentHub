package com.talenthub.service;

import com.talenthub.dto.ApplicationRequestDto;
import com.talenthub.dto.ArtistApplicationResponseDto;
import com.talenthub.dto.OrganizerApplicationResponseDto;
import com.talenthub.entity.*;
import com.talenthub.repository.ArtistProfileRepository;
import com.talenthub.repository.AuditionApplicationRepository;
import com.talenthub.repository.AuditionRepository;
import com.talenthub.service.impl.AuditionApplicationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditionApplicationServiceTest {

    @Mock
    private AuditionApplicationRepository auditionApplicationRepository;

    @Mock
    private AuditionRepository auditionRepository;

    @Mock
    private ArtistProfileRepository artistProfileRepository;

    @InjectMocks
    private AuditionApplicationServiceImpl applicationService;

    @Test
    void testApplyToActiveAuditionSuccess() {
        // Arrange (Req 1: Artist successfully applies to an active audition)
        User artistUser = User.builder().id(1L).role(Role.ARTIST).build();
        ArtistProfile artistProfile = ArtistProfile.builder().id(10L).user(artistUser).build();
        Audition audition = Audition.builder().id(100L).status("ACTIVE").build();
        ApplicationRequestDto requestDto = new ApplicationRequestDto("Hello!");

        when(auditionRepository.findById(100L)).thenReturn(Optional.of(audition));
        when(artistProfileRepository.findByUserId(1L)).thenReturn(Optional.of(artistProfile));
        when(auditionApplicationRepository.existsByAuditionIdAndArtistId(100L, 10L)).thenReturn(false);
        when(auditionApplicationRepository.save(any(AuditionApplication.class))).thenAnswer(i -> {
            AuditionApplication app = (AuditionApplication) i.getArguments()[0];
            app.setId(500L);
            return app;
        });

        // Act
        ArtistApplicationResponseDto response = applicationService.applyToAudition(1L, 100L, requestDto);

        // Assert
        assertNotNull(response);
        assertEquals(500L, response.getId());
        assertEquals("PENDING", response.getStatus());
        assertEquals("Hello!", response.getMessage());
    }

    @Test
    void testApplyTwiceToSameAuditionThrowsException() {
        // Arrange (Req 2: Artist cannot apply twice to the same audition)
        User artistUser = User.builder().id(1L).role(Role.ARTIST).build();
        ArtistProfile artistProfile = ArtistProfile.builder().id(10L).user(artistUser).build();
        Audition audition = Audition.builder().id(100L).status("ACTIVE").build();
        ApplicationRequestDto requestDto = new ApplicationRequestDto("Hello!");

        when(auditionRepository.findById(100L)).thenReturn(Optional.of(audition));
        when(artistProfileRepository.findByUserId(1L)).thenReturn(Optional.of(artistProfile));
        when(auditionApplicationRepository.existsByAuditionIdAndArtistId(100L, 10L)).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> applicationService.applyToAudition(1L, 100L, requestDto));
    }

    @Test
    void testApplyToClosedAuditionThrowsException() {
        // Arrange (Req 3: Artist cannot apply to a closed audition)
        Audition audition = Audition.builder().id(100L).status("CLOSED").build();
        ApplicationRequestDto requestDto = new ApplicationRequestDto("Hello!");

        when(auditionRepository.findById(100L)).thenReturn(Optional.of(audition));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> applicationService.applyToAudition(1L, 100L, requestDto));
    }

    @Test
    void testOrganizerViewApplicationsForOwnAuditionSuccess() {
        // Arrange (Req 4: Organizer can view applications for their own audition)
        User orgUser = User.builder().id(2L).role(Role.ORGANIZER).build();
        OrganizerProfile organizer = OrganizerProfile.builder().id(20L).user(orgUser).build();
        Audition audition = Audition.builder().id(100L).organizer(organizer).build();

        User artistUser = User.builder().email("artist@test.com").build();
        ArtistProfile artist = ArtistProfile.builder().id(10L).user(artistUser).build();
        AuditionApplication application = AuditionApplication.builder()
                .id(500L)
                .audition(audition)
                .artist(artist)
                .status("PENDING")
                .build();

        when(auditionRepository.findById(100L)).thenReturn(Optional.of(audition));
        when(auditionApplicationRepository.findByAuditionId(100L)).thenReturn(Collections.singletonList(application));

        // Act
        List<OrganizerApplicationResponseDto> results = applicationService.getApplicationsForAudition(2L, 100L);

        // Assert
        assertEquals(1, results.size());
        assertEquals("artist@test.com", results.get(0).getArtistName());
    }

    @Test
    void testOrganizerViewApplicationsForOtherAuditionThrowsException() {
        // Arrange (Req 5: Organizer cannot view applications for another organizer's audition)
        User ownerUser = User.builder().id(2L).role(Role.ORGANIZER).build();
        OrganizerProfile owner = OrganizerProfile.builder().id(20L).user(ownerUser).build();
        Audition audition = Audition.builder().id(100L).organizer(owner).build();

        when(auditionRepository.findById(100L)).thenReturn(Optional.of(audition));

        // Act & Assert (Attempting to view using organizerUserId 3)
        assertThrows(RuntimeException.class, () -> applicationService.getApplicationsForAudition(3L, 100L));
    }

    @Test
    void testOrganizerChangeApplicationStatusSuccess() {
        // Arrange (Req 6: Organizer can change application status)
        User orgUser = User.builder().id(2L).role(Role.ORGANIZER).build();
        OrganizerProfile organizer = OrganizerProfile.builder().id(20L).user(orgUser).build();
        Audition audition = Audition.builder().id(100L).organizer(organizer).build();
        AuditionApplication application = AuditionApplication.builder()
                .id(500L)
                .audition(audition)
                .status("PENDING")
                .build();

        when(auditionApplicationRepository.findById(500L)).thenReturn(Optional.of(application));

        // Act
        applicationService.updateApplicationStatus(2L, 500L, "SHORTLISTED");

        // Assert
        assertEquals("SHORTLISTED", application.getStatus());
        verify(auditionApplicationRepository, times(1)).save(application);
    }

    @Test
    void testOrganizerChangeApplicationStatusOtherOrganizerThrowsException() {
        // Arrange (Req 6: Organizer cannot modify applications belonging to another organizer's audition)
        User ownerUser = User.builder().id(2L).role(Role.ORGANIZER).build();
        OrganizerProfile owner = OrganizerProfile.builder().id(20L).user(ownerUser).build();
        Audition audition = Audition.builder().id(100L).organizer(owner).build();
        AuditionApplication application = AuditionApplication.builder()
                .id(500L)
                .audition(audition)
                .status("PENDING")
                .build();

        when(auditionApplicationRepository.findById(500L)).thenReturn(Optional.of(application));

        // Act & Assert (Attempting to change status using organizerUserId 3)
        assertThrows(RuntimeException.class, () -> applicationService.updateApplicationStatus(3L, 500L, "SELECTED"));
    }

    @Test
    void testArtistViewOwnApplicationsSuccess() {
        // Arrange (Req 9: Artist can view their own applications)
        User artistUser = User.builder().id(1L).role(Role.ARTIST).build();
        ArtistProfile artistProfile = ArtistProfile.builder().id(10L).user(artistUser).build();
        Audition audition = Audition.builder().id(100L).title("Actor Hunt").build();
        AuditionApplication application = AuditionApplication.builder()
                .id(500L)
                .audition(audition)
                .artist(artistProfile)
                .build();

        when(artistProfileRepository.findByUserId(1L)).thenReturn(Optional.of(artistProfile));
        when(auditionApplicationRepository.findByArtistId(10L)).thenReturn(Collections.singletonList(application));

        // Act
        List<ArtistApplicationResponseDto> results = applicationService.getMyApplications(1L);

        // Assert
        assertEquals(1, results.size());
        assertEquals("Actor Hunt", results.get(0).getAuditionTitle());
    }

    @Test
    void testArtistViewOtherArtistApplicationThrowsException() {
        // Arrange (Req 10: Another artist cannot access another artist's application)
        User ownerUser = User.builder().id(1L).role(Role.ARTIST).build();
        ArtistProfile ownerProfile = ArtistProfile.builder().id(10L).user(ownerUser).build();
        
        User strangerUser = User.builder().id(3L).role(Role.ARTIST).build();
        ArtistProfile strangerProfile = ArtistProfile.builder().id(30L).user(strangerUser).build();

        AuditionApplication application = AuditionApplication.builder()
                .id(500L)
                .artist(ownerProfile)
                .build();

        when(auditionApplicationRepository.findById(500L)).thenReturn(Optional.of(application));
        when(artistProfileRepository.findByUserId(3L)).thenReturn(Optional.of(strangerProfile));

        // Act & Assert (Attempting to read application using artistUserId 3)
        assertThrows(RuntimeException.class, () -> applicationService.getApplicationById(3L, 500L));
    }
}
