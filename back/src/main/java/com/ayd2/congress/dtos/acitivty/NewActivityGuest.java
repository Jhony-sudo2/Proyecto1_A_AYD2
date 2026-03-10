package com.ayd2.congress.dtos.acitivty;

import java.time.LocalDateTime;

import com.ayd2.congress.models.Enums.ActivityType;

import lombok.Value;

@Value
public class NewActivityGuest {
    private String name;
    private Long roomId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer capacity;
    private Long[] users;
    private Long congressId;
    private String description;
    private ActivityType type;
}
