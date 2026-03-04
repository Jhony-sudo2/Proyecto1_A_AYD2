package com.ayd2.congress.dtos.Congress;

import java.time.LocalDateTime;

import lombok.Value;

@Value
public class NewCongressRequest {
    private String name;
    private String description;
    private Double price;
    private Long organizationId;
    private Long locationId;
    private LocalDateTime endCallDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
