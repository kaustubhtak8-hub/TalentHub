package com.talenthub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizerApplicationResponseDto {
    private Long id;
    private String artistName;
    private String artistLocation;
    private String artistBio;
    private String artistExperience;
    private String artistPhone;
    private String artistProfileImageUrl;
    private String message;
    private String status;
    private LocalDateTime appliedAt;
}
