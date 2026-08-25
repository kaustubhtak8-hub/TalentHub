package com.talenthub.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusUpdateRequestDto {
    @NotBlank(message = "Status is required")
    private String status; // "PENDING", "SHORTLISTED", "REJECTED", "SELECTED"
}
