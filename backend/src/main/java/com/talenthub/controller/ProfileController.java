package com.talenthub.controller;

import com.talenthub.dto.ArtistProfileDto;
import com.talenthub.dto.OrganizerProfileDto;
import com.talenthub.security.UserPrincipal;
import com.talenthub.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profiles")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping("/artist")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<ArtistProfileDto> getArtistProfile(@AuthenticationPrincipal UserPrincipal principal) {
        ArtistProfileDto profile = profileService.getArtistProfile(principal.getId());
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/artist")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<ArtistProfileDto> updateArtistProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ArtistProfileDto dto) {
        ArtistProfileDto updated = profileService.updateArtistProfile(principal.getId(), dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/organizer")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<OrganizerProfileDto> getOrganizerProfile(@AuthenticationPrincipal UserPrincipal principal) {
        OrganizerProfileDto profile = profileService.getOrganizerProfile(principal.getId());
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/organizer")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<OrganizerProfileDto> updateOrganizerProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody OrganizerProfileDto dto) {
        OrganizerProfileDto updated = profileService.updateOrganizerProfile(principal.getId(), dto);
        return ResponseEntity.ok(updated);
    }
}
