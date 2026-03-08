package com.ayd2.congress.dtos.acitivty;

import java.time.LocalDateTime;

import lombok.Value;

@Value
public class UpdateActivity {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long roomId;
    private Long capacity;
    private String name;
    private String description;
}
