package com.talenthub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtistApplicationResponseDto {
    private Long id;
    private Long auditionId;
    private String auditionTitle;
    private String auditionLocation;
    private LocalDate auditionDate;
    private String message;
    private String status;
    private LocalDateTime appliedAt;
}
