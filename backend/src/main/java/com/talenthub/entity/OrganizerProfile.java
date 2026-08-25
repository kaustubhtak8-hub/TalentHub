package com.talenthub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "organizer_profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "organization_name", nullable = false)
    private String organizationName;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String location;

    private String website;
}
