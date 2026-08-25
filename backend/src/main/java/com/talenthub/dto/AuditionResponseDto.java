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
public class AuditionResponseDto {
    private Long id;
    private Long organizerId;
    private String organizationName;
    private String title;
    private String description;
    private String category;
    private String location;
    private LocalDate auditionDate;
    private LocalDate applicationDeadline;
    private String requirements;
    private String status;
    private LocalDateTime createdAt;
}
