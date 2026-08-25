package com.talenthub.repository;

import com.talenthub.entity.Audition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditionRepository extends JpaRepository<Audition, Long> {
    List<Audition> findByOrganizerId(Long organizerId);
    List<Audition> findByStatus(String status);
}
