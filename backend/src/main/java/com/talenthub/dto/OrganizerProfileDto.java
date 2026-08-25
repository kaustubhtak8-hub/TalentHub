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
public class OrganizerProfileDto {
    @NotBlank(message = "Organization name is required")
    private String organizationName;
    
    private String description;
    private String location;
    private String website;
}
