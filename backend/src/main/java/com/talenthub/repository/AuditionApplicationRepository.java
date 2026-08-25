package com.talenthub.repository;

import com.talenthub.entity.AuditionApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuditionApplicationRepository extends JpaRepository<AuditionApplication, Long> {
    List<AuditionApplication> findByArtistId(Long artistProfileId);
    List<AuditionApplication> findByAuditionId(Long auditionId);
    Boolean existsByAuditionIdAndArtistId(Long auditionId, Long artistProfileId);
    Optional<AuditionApplication> findByAuditionIdAndArtistId(Long auditionId, Long artistProfileId);
}
