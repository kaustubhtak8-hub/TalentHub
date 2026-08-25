package com.talenthub.service;

import com.talenthub.dto.ArtistProfileDto;
import com.talenthub.dto.OrganizerProfileDto;
import com.talenthub.entity.ArtistProfile;
import com.talenthub.entity.OrganizerProfile;
import com.talenthub.entity.Role;
import com.talenthub.entity.User;
import com.talenthub.repository.ArtistProfileRepository;
import com.talenthub.repository.OrganizerProfileRepository;
import com.talenthub.repository.UserRepository;
import com.talenthub.service.impl.ProfileServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private ArtistProfileRepository artistProfileRepository;

    @Mock
    private OrganizerProfileRepository organizerProfileRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProfileServiceImpl profileService;

    @Test
    void testGetArtistProfileSuccess() {
        // Arrange
        User user = User.builder().id(1L).email("artist@test.com").role(Role.ARTIST).build();
        ArtistProfile profile = ArtistProfile.builder()
                .id(10L)
                .user(user)
                .bio("I am a theater actor")
                .phone("123456789")
                .location("London")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(artistProfileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));

        // Act
        ArtistProfileDto result = profileService.getArtistProfile(1L);

        // Assert
        assertEquals("I am a theater actor", result.getBio());
        assertEquals("123456789", result.getPhone());
    }

    @Test
    void testGetArtistProfileRoleMismatchThrowsException() {
        // Arrange
        User user = User.builder().id(1L).email("wrong@test.com").role(Role.ORGANIZER).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> profileService.getArtistProfile(1L));
    }

    @Test
    void testUpdateArtistProfileSuccess() {
        // Arrange
        User user = User.builder().id(1L).email("artist@test.com").role(Role.ARTIST).build();
        ArtistProfile profile = ArtistProfile.builder().user(user).build();
        ArtistProfileDto dto = ArtistProfileDto.builder()
                .bio("New Bio")
                .phone("987654321")
                .location("Paris")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(artistProfileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));
        when(artistProfileRepository.save(any(ArtistProfile.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        ArtistProfileDto result = profileService.updateArtistProfile(1L, dto);

        // Assert
        assertEquals("New Bio", result.getBio());
        assertEquals("987654321", result.getPhone());
        verify(artistProfileRepository, times(1)).save(any(ArtistProfile.class));
    }

    @Test
    void testGetOrganizerProfileSuccess() {
        // Arrange
        User user = User.builder().id(2L).email("org@test.com").role(Role.ORGANIZER).build();
        OrganizerProfile profile = OrganizerProfile.builder()
                .id(20L)
                .user(user)
                .organizationName("Star Agency")
                .build();

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(organizerProfileRepository.findByUserId(2L)).thenReturn(Optional.of(profile));

        // Act
        OrganizerProfileDto result = profileService.getOrganizerProfile(2L);

        // Assert
        assertEquals("Star Agency", result.getOrganizationName());
    }

    @Test
    void testUpdateOrganizerProfileSuccess() {
        // Arrange
        User user = User.builder().id(2L).email("org@test.com").role(Role.ORGANIZER).build();
        OrganizerProfile profile = OrganizerProfile.builder().user(user).build();
        OrganizerProfileDto dto = OrganizerProfileDto.builder()
                .organizationName("Star Productions")
                .description("Film Studio")
                .build();

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(organizerProfileRepository.findByUserId(2L)).thenReturn(Optional.of(profile));
        when(organizerProfileRepository.save(any(OrganizerProfile.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        OrganizerProfileDto result = profileService.updateOrganizerProfile(2L, dto);

        // Assert
        assertEquals("Star Productions", result.getOrganizationName());
        verify(organizerProfileRepository, times(1)).save(any(OrganizerProfile.class));
    }
}
