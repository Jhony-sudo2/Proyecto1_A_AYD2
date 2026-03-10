package com.ayd2.congress.dtos.acitivty;

import java.time.LocalDateTime;

import lombok.Value;

@Value
public class NewActivityRequest {
    private String name;
    private Long roomId;
    private Long proposalId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer capacity;
}
