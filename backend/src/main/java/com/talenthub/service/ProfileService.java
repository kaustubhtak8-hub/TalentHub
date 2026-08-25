package com.talenthub.service;

import com.talenthub.dto.ArtistProfileDto;
import com.talenthub.dto.OrganizerProfileDto;

public interface ProfileService {
    ArtistProfileDto getArtistProfile(Long userId);
    ArtistProfileDto updateArtistProfile(Long userId, ArtistProfileDto dto);
    OrganizerProfileDto getOrganizerProfile(Long userId);
    OrganizerProfileDto updateOrganizerProfile(Long userId, OrganizerProfileDto dto);
}
