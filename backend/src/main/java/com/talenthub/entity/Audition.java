package com.talenthub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Audition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_profile_id", nullable = false)
    private OrganizerProfile organizer;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String location;

    @Column(name = "audition_date")
    private LocalDate auditionDate;

    @Column(name = "application_deadline", nullable = false)
    private LocalDate applicationDeadline;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    @Column(nullable = false)
    private String status; // "ACTIVE", "CLOSED"

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = "ACTIVE";
        }
        createdAt = LocalDateTime.now();
    }
}
