package com.talenthub.service;

import com.talenthub.dto.ApplicationRequestDto;
import com.talenthub.dto.ArtistApplicationResponseDto;
import com.talenthub.dto.OrganizerApplicationResponseDto;
import java.util.List;

public interface AuditionApplicationService {
    ArtistApplicationResponseDto applyToAudition(Long artistUserId, Long auditionId, ApplicationRequestDto dto);
    List<ArtistApplicationResponseDto> getMyApplications(Long artistUserId);
    ArtistApplicationResponseDto getApplicationById(Long artistUserId, Long applicationId);
    List<OrganizerApplicationResponseDto> getApplicationsForAudition(Long organizerUserId, Long auditionId);
    void updateApplicationStatus(Long organizerUserId, Long applicationId, String newStatus);
}
