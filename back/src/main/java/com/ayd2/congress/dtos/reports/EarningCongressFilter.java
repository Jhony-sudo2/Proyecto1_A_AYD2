package com.ayd2.congress.dtos.reports;

import java.time.LocalDateTime;

import lombok.Value;

@Value
public class EarningCongressFilter {
    private Long congressId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
