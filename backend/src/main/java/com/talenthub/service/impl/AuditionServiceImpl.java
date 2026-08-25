package com.talenthub.service.impl;

import com.talenthub.dto.AuditionRequestDto;
import com.talenthub.dto.AuditionResponseDto;
import com.talenthub.entity.Audition;
import com.talenthub.entity.OrganizerProfile;
import com.talenthub.repository.AuditionRepository;
import com.talenthub.repository.OrganizerProfileRepository;
import com.talenthub.service.AuditionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditionServiceImpl implements AuditionService {

    @Autowired
    private AuditionRepository auditionRepository;

    @Autowired
    private OrganizerProfileRepository organizerProfileRepository;

    private static final List<String> PREDEFINED_CATEGORIES = Arrays.asList(
            "Acting", "Singing", "Dancing", "Comedy", "Theatre", "Modeling", "Music", "Anchoring"
    );

    @Override
    @Transactional
    public AuditionResponseDto createAudition(Long organizerUserId, AuditionRequestDto dto) {
        validateCategory(dto.getCategory());
        
        OrganizerProfile organizer = organizerProfileRepository.findByUserId(organizerUserId)
                .orElseThrow(() -> new RuntimeException("Organizer profile not found. Please create your profile details first."));

        Audition audition = Audition.builder()
                .organizer(organizer)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .location(dto.getLocation())
                .auditionDate(dto.getAuditionDate())
                .applicationDeadline(dto.getApplicationDeadline())
                .requirements(dto.getRequirements())
                .status("ACTIVE")
                .build();

        Audition saved = auditionRepository.save(audition);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditionResponseDto> getMyAuditions(Long organizerUserId) {
        OrganizerProfile organizer = organizerProfileRepository.findByUserId(organizerUserId)
                .orElseThrow(() -> new RuntimeException("Organizer profile not found."));

        return auditionRepository.findByOrganizerId(organizer.getId()).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AuditionResponseDto updateAudition(Long organizerUserId, Long auditionId, AuditionRequestDto dto) {
        validateCategory(dto.getCategory());

        Audition audition = auditionRepository.findById(auditionId)
                .orElseThrow(() -> new RuntimeException("Audition post not found with ID: " + auditionId));

        validateOwnership(organizerUserId, audition);

        audition.setTitle(dto.getTitle());
        audition.setDescription(dto.getDescription());
        audition.setCategory(dto.getCategory());
        audition.setLocation(dto.getLocation());
        audition.setAuditionDate(dto.getAuditionDate());
        audition.setApplicationDeadline(dto.getApplicationDeadline());
        audition.setRequirements(dto.getRequirements());
        if (dto.getStatus() != null) {
            audition.setStatus(dto.getStatus());
        }

        Audition updated = auditionRepository.save(audition);
        return mapToResponseDto(updated);
    }

    @Override
    @Transactional
    public void deleteAudition(Long organizerUserId, Long auditionId) {
        Audition audition = auditionRepository.findById(auditionId)
                .orElseThrow(() -> new RuntimeException("Audition post not found with ID: " + auditionId));

        validateOwnership(organizerUserId, audition);
        auditionRepository.delete(audition);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditionResponseDto> getActiveAuditions() {
        return auditionRepository.findByStatus("ACTIVE").stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AuditionResponseDto getAuditionDetails(Long auditionId) {
        Audition audition = auditionRepository.findById(auditionId)
                .orElseThrow(() -> new RuntimeException("Audition post not found with ID: " + auditionId));
        return mapToResponseDto(audition);
    }

    private void validateCategory(String category) {
        if (!PREDEFINED_CATEGORIES.contains(category)) {
            throw new RuntimeException("Invalid category. Allowed categories are: " + String.join(", ", PREDEFINED_CATEGORIES));
        }
    }

    private void validateOwnership(Long organizerUserId, Audition audition) {
        if (!audition.getOrganizer().getUser().getId().equals(organizerUserId)) {
            throw new RuntimeException("Unauthorized: You do not own this audition post.");
        }
    }

    private AuditionResponseDto mapToResponseDto(Audition audition) {
        return AuditionResponseDto.builder()
                .id(audition.getId())
                .organizerId(audition.getOrganizer().getId())
                .organizationName(audition.getOrganizer().getOrganizationName())
                .title(audition.getTitle())
                .description(audition.getDescription())
                .category(audition.getCategory())
                .location(audition.getLocation())
                .auditionDate(audition.getAuditionDate())
                .applicationDeadline(audition.getApplicationDeadline())
                .requirements(audition.getRequirements())
                .status(audition.getStatus())
                .createdAt(audition.getCreatedAt())
                .build();
    }
}
