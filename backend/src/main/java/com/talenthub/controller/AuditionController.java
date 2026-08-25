package com.talenthub.controller;

import com.talenthub.dto.AuditionRequestDto;
import com.talenthub.dto.AuditionResponseDto;
import com.talenthub.security.UserPrincipal;
import com.talenthub.service.AuditionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/auditions")
public class AuditionController {

    @Autowired
    private AuditionService auditionService;

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<AuditionResponseDto> createAudition(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody AuditionRequestDto dto) {
        AuditionResponseDto created = auditionService.createAudition(principal.getId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<List<AuditionResponseDto>> getMyAuditions(@AuthenticationPrincipal UserPrincipal principal) {
        List<AuditionResponseDto> myAuditions = auditionService.getMyAuditions(principal.getId());
        return ResponseEntity.ok(myAuditions);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<AuditionResponseDto> updateAudition(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody AuditionRequestDto dto) {
        AuditionResponseDto updated = auditionService.updateAudition(principal.getId(), id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<Void> deleteAudition(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        auditionService.deleteAudition(principal.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<AuditionResponseDto>> getActiveAuditions() {
        List<AuditionResponseDto> activeAuditions = auditionService.getActiveAuditions();
        return ResponseEntity.ok(activeAuditions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditionResponseDto> getAuditionDetails(@PathVariable Long id) {
        AuditionResponseDto details = auditionService.getAuditionDetails(id);
        return ResponseEntity.ok(details);
    }
}
