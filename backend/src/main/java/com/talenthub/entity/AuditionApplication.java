package com.talenthub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "audition_applications",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"audition_id", "artist_profile_id"})
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditionApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audition_id", nullable = false)
    private Audition audition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_profile_id", nullable = false)
    private ArtistProfile artist;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private String status; // "PENDING", "SHORTLISTED", "REJECTED", "SELECTED"

    @Column(name = "applied_at", nullable = false, updatable = false)
    private LocalDateTime appliedAt;

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = "PENDING";
        }
        appliedAt = LocalDateTime.now();
    }
}
