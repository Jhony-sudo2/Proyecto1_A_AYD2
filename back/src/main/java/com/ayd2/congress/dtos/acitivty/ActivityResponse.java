package com.ayd2.congress.dtos.acitivty;

import java.time.LocalDateTime;

import com.ayd2.congress.models.Enums.ActivityType;

import lombok.Value;

@Value
public class ActivityResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private ActivityType type;
    private Integer capacity;
}
