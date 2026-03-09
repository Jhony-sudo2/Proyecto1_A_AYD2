package com.ayd2.congress.repositories.Activity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ayd2.congress.compositePrimaryKeys.SpeakerId;
import com.ayd2.congress.models.Activities.SpeakerEntity;

@Repository
public interface SpeakerRepository extends JpaRepository<SpeakerEntity, SpeakerId> {
    boolean existsByUserIdAndActivityId(Long userId, Long activityId);
    List<SpeakerEntity> findAllByActivityId(Long activityId);

    @Query("""
                SELECT COUNT(s) > 0
                FROM SpeakerEntity s
                WHERE s.user.id = :userId
                AND s.activity.startDate < :endDate
                AND s.activity.endDate > :startDate
            """)
    boolean existsSpeakerConflict(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("""
                SELECT COUNT(s) > 0
                FROM SpeakerEntity s
                WHERE s.user.id = :userId
                AND s.activity.id <> :activityId
                AND s.activity.startDate < :endDate
                AND s.activity.endDate > :startDate
            """)
    boolean existsSpeakerConflictExcludingActivity(
            Long userId,
            Long activityId,
            LocalDateTime startDate,
            LocalDateTime endDate);

}
