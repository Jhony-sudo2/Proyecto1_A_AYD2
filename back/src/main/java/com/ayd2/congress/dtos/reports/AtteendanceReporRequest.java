package com.ayd2.congress.dtos.reports;

import java.time.LocalDateTime;

import lombok.Value;

@Value
public class AtteendanceReporRequest {
    private Long activityId;
    private Long roomId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
