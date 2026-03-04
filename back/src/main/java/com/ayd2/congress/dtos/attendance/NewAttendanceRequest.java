package com.ayd2.congress.dtos.attendance;
import lombok.Value;

@Value
public class NewAttendanceRequest {
    private Long activityId;
    private Long userId;
}
