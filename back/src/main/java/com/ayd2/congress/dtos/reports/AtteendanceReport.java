package com.ayd2.congress.dtos.reports;

import java.time.LocalDateTime;

import lombok.Value;


@Value
public class AtteendanceReport {
    private String activityName;
    private String roomName;
    private LocalDateTime startDate;
    private Long atteendances;

}
