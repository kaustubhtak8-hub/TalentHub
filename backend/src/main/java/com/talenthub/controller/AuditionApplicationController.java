package com.talenthub.controller;

import com.talenthub.dto.ApiResponse;
import com.talenthub.dto.ApplicationRequestDto;
import com.talenthub.dto.ArtistApplicationResponseDto;
import com.talenthub.dto.OrganizerApplicationResponseDto;
import com.talenthub.dto.StatusUpdateRequestDto;
import com.talenthub.security.UserPrincipal;
import com.talenthub.service.AuditionApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping
public class AuditionApplicationController {

    @Autowired
    private AuditionApplicationService applicationService;

    @PostMapping("/auditions/{auditionId}/applications")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<ArtistApplicationResponseDto> applyToAudition(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long auditionId,
            @Valid @RequestBody ApplicationRequestDto dto) {
        ArtistApplicationResponseDto response = applicationService.applyToAudition(principal.getId(), auditionId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/applications/my")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<List<ArtistApplicationResponseDto>> getMyApplications(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<ArtistApplicationResponseDto> myApplications = applicationService.getMyApplications(principal.getId());
        return ResponseEntity.ok(myApplications);
    }

    @GetMapping("/applications/{id}")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<ArtistApplicationResponseDto> getApplicationById(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        ArtistApplicationResponseDto response = applicationService.getApplicationById(principal.getId(), id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/auditions/{auditionId}/applications")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<List<OrganizerApplicationResponseDto>> getApplicationsForAudition(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long auditionId) {
        List<OrganizerApplicationResponseDto> list = applicationService.getApplicationsForAudition(principal.getId(), auditionId);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/applications/{id}/status")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<ApiResponse> updateApplicationStatus(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequestDto dto) {
        applicationService.updateApplicationStatus(principal.getId(), id, dto.getStatus());
        return ResponseEntity.ok(new ApiResponse(true, "Application status updated successfully"));
    }
}
