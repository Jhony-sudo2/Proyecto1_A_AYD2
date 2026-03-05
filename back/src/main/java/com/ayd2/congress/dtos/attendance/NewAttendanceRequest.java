package com.ayd2.congress.dtos.attendance;
import java.time.LocalDateTime;

import com.ayd2.congress.models.Enums.AttendanceType;

import lombok.Value;

@Value
public class NewAttendanceRequest {
    private Long activityId;
    private String userIdentification;
    private LocalDateTime date;
    private AttendanceType type;
}
