package com.ayd2.congress.repositories.Attendance;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayd2.congress.compositePrimaryKeys.AttendanceId;
import com.ayd2.congress.models.Attendance.AttendanceEntity;
import com.ayd2.congress.models.Enums.AttendanceType;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceEntity,AttendanceId>{
    boolean existsByActivityIdAndUserIdAndType(Long activityId, Long userId, AttendanceType type);
    Long countByActivityIdAndType(Long activityId, AttendanceType type);
    List<AttendanceEntity> findByUserId(Long userId);
    List<AttendanceEntity> findByActivityId(Long activityId);
}
