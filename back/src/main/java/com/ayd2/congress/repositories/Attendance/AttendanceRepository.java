package com.ayd2.congress.repositories.Attendance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayd2.congress.compositePrimaryKeys.AttendanceId;
import com.ayd2.congress.models.Attendance.AttendanceEntity;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceEntity,AttendanceId>{
    
}
