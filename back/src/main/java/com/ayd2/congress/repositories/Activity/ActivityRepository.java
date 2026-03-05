package com.ayd2.congress.repositories.Activity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ayd2.congress.models.Activities.ActivityEntity;

@Repository
public interface ActivityRepository extends JpaRepository<ActivityEntity, Long> {
    @Query("""
        select (count(a) > 0)
        from ActivityEntity a
        where a.room.id = :roomId
          and a.startDate < :endDate
          and a.endDate > :startDate
    """)
    boolean existsOverlap(Long roomId, LocalDateTime startDate, LocalDateTime endDate);
    boolean existsByProposalId(Long proposalId);
    List<ActivityEntity> findByProposalCongressId(Long congressId);
    List<ActivityEntity> findByTypeAndProposalCongressId(com.ayd2.congress.models.Enums.ActivityType type, Long congressId);
}
