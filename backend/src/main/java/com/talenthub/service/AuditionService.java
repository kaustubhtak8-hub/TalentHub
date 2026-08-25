package com.talenthub.service;

import com.talenthub.dto.AuditionRequestDto;
import com.talenthub.dto.AuditionResponseDto;
import java.util.List;

public interface AuditionService {
    AuditionResponseDto createAudition(Long organizerUserId, AuditionRequestDto dto);
    List<AuditionResponseDto> getMyAuditions(Long organizerUserId);
    AuditionResponseDto updateAudition(Long organizerUserId, Long auditionId, AuditionRequestDto dto);
    void deleteAudition(Long organizerUserId, Long auditionId);
    List<AuditionResponseDto> getActiveAuditions();
    AuditionResponseDto getAuditionDetails(Long auditionId);
}
