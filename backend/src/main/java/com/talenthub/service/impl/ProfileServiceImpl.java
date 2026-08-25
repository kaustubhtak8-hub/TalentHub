package com.talenthub.service.impl;

import com.talenthub.dto.ArtistProfileDto;
import com.talenthub.dto.OrganizerProfileDto;
import com.talenthub.entity.ArtistProfile;
import com.talenthub.entity.OrganizerProfile;
import com.talenthub.entity.Role;
import com.talenthub.entity.User;
import com.talenthub.repository.ArtistProfileRepository;
import com.talenthub.repository.OrganizerProfileRepository;
import com.talenthub.repository.UserRepository;
import com.talenthub.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private ArtistProfileRepository artistProfileRepository;

    @Autowired
    private OrganizerProfileRepository organizerProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public ArtistProfileDto getArtistProfile(Long userId) {
        User user = getUserAndValidateRole(userId, Role.ARTIST);
        ArtistProfile profile = artistProfileRepository.findByUserId(userId)
                .orElse(new ArtistProfile()); // Return blank profile if not created yet
        
        return mapToArtistDto(profile);
    }

    @Override
    @Transactional
    public ArtistProfileDto updateArtistProfile(Long userId, ArtistProfileDto dto) {
        User user = getUserAndValidateRole(userId, Role.ARTIST);
        ArtistProfile profile = artistProfileRepository.findByUserId(userId)
                .orElseGet(() -> ArtistProfile.builder().user(user).build());

        profile.setBio(dto.getBio());
        profile.setPhone(dto.getPhone());
        profile.setLocation(dto.getLocation());
        profile.setExperience(dto.getExperience());
        profile.setProfileImageUrl(dto.getProfileImageUrl());

        ArtistProfile saved = artistProfileRepository.save(profile);
        return mapToArtistDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrganizerProfileDto getOrganizerProfile(Long userId) {
        User user = getUserAndValidateRole(userId, Role.ORGANIZER);
        OrganizerProfile profile = organizerProfileRepository.findByUserId(userId)
                .orElse(new OrganizerProfile());

        return mapToOrganizerDto(profile);
    }

    @Override
    @Transactional
    public OrganizerProfileDto updateOrganizerProfile(Long userId, OrganizerProfileDto dto) {
        User user = getUserAndValidateRole(userId, Role.ORGANIZER);
        OrganizerProfile profile = organizerProfileRepository.findByUserId(userId)
                .orElseGet(() -> OrganizerProfile.builder().user(user).build());

        profile.setOrganizationName(dto.getOrganizationName());
        profile.setDescription(dto.getDescription());
        profile.setLocation(dto.getLocation());
        profile.setWebsite(dto.getWebsite());

        OrganizerProfile saved = organizerProfileRepository.save(profile);
        return mapToOrganizerDto(saved);
    }

    private User getUserAndValidateRole(Long userId, Role expectedRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        if (user.getRole() != expectedRole) {
            throw new RuntimeException("Unauthorized: User role is not " + expectedRole.name());
        }
        return user;
    }

    private ArtistProfileDto mapToArtistDto(ArtistProfile profile) {
        return ArtistProfileDto.builder()
                .bio(profile.getBio())
                .phone(profile.getPhone())
                .location(profile.getLocation())
                .experience(profile.getExperience())
                .profileImageUrl(profile.getProfileImageUrl())
                .build();
    }

    private OrganizerProfileDto mapToOrganizerDto(OrganizerProfile profile) {
        return OrganizerProfileDto.builder()
                .organizationName(profile.getOrganizationName() != null ? profile.getOrganizationName() : "")
                .description(profile.getDescription())
                .location(profile.getLocation())
                .website(profile.getWebsite())
                .build();
    }
}
