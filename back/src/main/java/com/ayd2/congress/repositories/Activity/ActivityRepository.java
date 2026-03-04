package com.ayd2.congress.repositories.Activity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayd2.congress.models.Activities.ActivityEntity;

@Repository
public interface ActivityRepository extends JpaRepository<ActivityEntity, Long> {
    boolean existsByRoomIdAndTimeRange(Long roomId, java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
    boolean existsByProposalId(Long proposalId);
    List<ActivityEntity> findByCongressId(Long congressId);
    List<ActivityEntity> findByTypeAndCongressId(com.ayd2.congress.models.Enums.ActivityType type, Long congressId);
}
