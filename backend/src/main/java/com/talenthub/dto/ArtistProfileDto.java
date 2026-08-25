package com.talenthub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtistProfileDto {
    private String bio;
    private String phone;
    private String location;
    private String experience;
    private String profileImageUrl;
}
