package com.ayd2.congress.repositories.Attendance;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ayd2.congress.compositePrimaryKeys.AttendanceId;
import com.ayd2.congress.dtos.reports.AtteendanceReport;
import com.ayd2.congress.dtos.reports.WorkshopParticipant;
import com.ayd2.congress.models.Activities.ActivityEntity;
import com.ayd2.congress.models.Attendance.AttendanceEntity;
import com.ayd2.congress.models.Enums.AttendanceType;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceEntity, AttendanceId> {
    boolean existsByActivityIdAndUserIdAndType(Long activityId, Long userId, AttendanceType type);

    Long countByActivityIdAndType(Long activityId, AttendanceType type);

    List<AttendanceEntity> findByUserId(Long userId);

    List<AttendanceEntity> findByActivityId(Long activityId);

    @Query("""
                SELECT a
                FROM ActivityEntity a
                WHERE a.proposal.congress.id = :congressId
                AND a.proposal.type = com.ayd2.congress.models.Enums.ActivityType.WORKSHOP
                AND (:activityId IS NULL OR a.id = :activityId)
            """)
    List<ActivityEntity> getWorkshops(
            @Param("congressId") Long congressId,
            @Param("activityId") Long activityId);

    // Query 2: participantes con reserva
    @Query("""
                SELECT new com.ayd2.congress.dtos.reports.WorkshopParticipant(
                    u.identification,
                    CONCAT(u.name, ' ', u.lastName),
                    u.email,
                    ar.name
                )
                FROM AttendanceEntity att
                JOIN att.activity a
                JOIN att.user u
                JOIN InscriptionEntity i ON i.user = u AND i.congress = a.proposal.congress
                JOIN i.attendeeRol ar
                WHERE att.type = com.ayd2.congress.models.Enums.AttendanceType.WORKSHOPINSCRIPTION
                AND a.id = :activityId
            """)
    List<WorkshopParticipant> getWorkshopParticipants(@Param("activityId") Long activityId);

    @Query("""
    SELECT new com.ayd2.congress.dtos.reports.AtteendanceReport(
        a.name,
        a.room.name,
        a.startDate,
        COUNT(att.id)
    )
    FROM AttendanceEntity att
    JOIN att.activity a
    WHERE att.type = com.ayd2.congress.models.Enums.AttendanceType.ATTENDANCE
    AND (:activityId IS NULL OR a.id = :activityId)
    AND (:roomId IS NULL OR a.room.id = :roomId)
    AND (:startDate IS NULL OR a.startDate >= :startDate)
    AND (:endDate IS NULL OR a.startDate <= :endDate)
    GROUP BY a.id, a.name, a.room.name, a.startDate
    ORDER BY a.startDate ASC
""")
List<AtteendanceReport> getAttendanceReport(
        @Param("activityId") Long activityId,
        @Param("roomId") Long roomId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);
}
