package com.talenthub.service.impl;

import com.talenthub.dto.ApplicationRequestDto;
import com.talenthub.dto.ArtistApplicationResponseDto;
import com.talenthub.dto.OrganizerApplicationResponseDto;
import com.talenthub.entity.ArtistProfile;
import com.talenthub.entity.Audition;
import com.talenthub.entity.AuditionApplication;
import com.talenthub.repository.ArtistProfileRepository;
import com.talenthub.repository.AuditionApplicationRepository;
import com.talenthub.repository.AuditionRepository;
import com.talenthub.service.AuditionApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditionApplicationServiceImpl implements AuditionApplicationService {

    @Autowired
    private AuditionApplicationRepository auditionApplicationRepository;

    @Autowired
    private AuditionRepository auditionRepository;

    @Autowired
    private ArtistProfileRepository artistProfileRepository;

    private static final List<String> ALLOWED_STATUSES = Arrays.asList(
            "PENDING", "SHORTLISTED", "REJECTED", "SELECTED"
    );

    @Override
    @Transactional
    public ArtistApplicationResponseDto applyToAudition(Long artistUserId, Long auditionId, ApplicationRequestDto dto) {
        Audition audition = auditionRepository.findById(auditionId)
                .orElseThrow(() -> new RuntimeException("Audition not found with ID: " + auditionId));

        if (!"ACTIVE".equalsIgnoreCase(audition.getStatus())) {
            throw new RuntimeException("Cannot apply to a CLOSED audition.");
        }

        ArtistProfile artistProfile = artistProfileRepository.findByUserId(artistUserId)
                .orElseThrow(() -> new RuntimeException("Artist profile not found. Please create your profile details first."));

        if (auditionApplicationRepository.existsByAuditionIdAndArtistId(auditionId, artistProfile.getId())) {
            throw new RuntimeException("You have already applied to this audition.");
        }

        AuditionApplication application = AuditionApplication.builder()
                .audition(audition)
                .artist(artistProfile)
                .message(dto.getMessage())
                .status("PENDING")
                .build();

        AuditionApplication saved = auditionApplicationRepository.save(application);
        return mapToArtistResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArtistApplicationResponseDto> getMyApplications(Long artistUserId) {
        ArtistProfile artistProfile = artistProfileRepository.findByUserId(artistUserId)
                .orElseThrow(() -> new RuntimeException("Artist profile not found. Please create your profile details first."));

        return auditionApplicationRepository.findByArtistId(artistProfile.getId()).stream()
                .map(this::mapToArtistResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ArtistApplicationResponseDto getApplicationById(Long artistUserId, Long applicationId) {
        AuditionApplication application = auditionApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found with ID: " + applicationId));

        ArtistProfile artistProfile = artistProfileRepository.findByUserId(artistUserId)
                .orElseThrow(() -> new RuntimeException("Artist profile not found."));

        if (!application.getArtist().getId().equals(artistProfile.getId())) {
            throw new RuntimeException("Unauthorized: You do not own this application.");
        }

        return mapToArtistResponseDto(application);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizerApplicationResponseDto> getApplicationsForAudition(Long organizerUserId, Long auditionId) {
        Audition audition = auditionRepository.findById(auditionId)
                .orElseThrow(() -> new RuntimeException("Audition not found with ID: " + auditionId));

        if (!audition.getOrganizer().getUser().getId().equals(organizerUserId)) {
            throw new RuntimeException("Unauthorized: You do not own this audition post.");
        }

        return auditionApplicationRepository.findByAuditionId(auditionId).stream()
                .map(this::mapToOrganizerResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateApplicationStatus(Long organizerUserId, Long applicationId, String newStatus) {
        if (!ALLOWED_STATUSES.contains(newStatus)) {
            throw new RuntimeException("Invalid application status. Allowed values are: " + String.join(", ", ALLOWED_STATUSES));
        }

        AuditionApplication application = auditionApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found with ID: " + applicationId));

        if (!application.getAudition().getOrganizer().getUser().getId().equals(organizerUserId)) {
            throw new RuntimeException("Unauthorized: You do not own the audition linked to this application.");
        }

        application.setStatus(newStatus);
        auditionApplicationRepository.save(application);
    }

    private ArtistApplicationResponseDto mapToArtistResponseDto(AuditionApplication application) {
        return ArtistApplicationResponseDto.builder()
                .id(application.getId())
                .auditionId(application.getAudition().getId())
                .auditionTitle(application.getAudition().getTitle())
                .auditionLocation(application.getAudition().getLocation())
                .auditionDate(application.getAudition().getAuditionDate())
                .message(application.getMessage())
                .status(application.getStatus())
                .appliedAt(application.getAppliedAt())
                .build();
    }

    private OrganizerApplicationResponseDto mapToOrganizerResponseDto(AuditionApplication application) {
        ArtistProfile artist = application.getArtist();
        return OrganizerApplicationResponseDto.builder()
                .id(application.getId())
                .artistName(artist.getUser().getEmail()) // Default name to email if profile full_name isn't present
                .artistLocation(artist.getLocation())
                .artistBio(artist.getBio())
                .artistExperience(artist.getExperience())
                .artistPhone(artist.getPhone())
                .artistProfileImageUrl(artist.getProfileImageUrl())
                .message(application.getMessage())
                .status(application.getStatus())
                .appliedAt(application.getAppliedAt())
                .build();
    }
}
