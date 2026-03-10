package com.ayd2.congress.dtos.attendance;
import java.time.LocalDateTime;

import com.ayd2.congress.models.Enums.AttendanceType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class NewAttendanceRequest {
    @NotNull(message = "activity is required")
    private Long activityId;
    @NotBlank(message = "user is required")
    private String userIdentification;
    @NotNull(message = "date is required")
    private LocalDateTime date;
    @NotNull(message = "atteendance type is required")
    private AttendanceType type;
}
