package com.ayd2.congress.dtos.Congress;

import java.time.LocalDate;

import lombok.Value;

@Value
public class NewCongressRequest {
    private String name;
    private String description;
    private Double price;
    private Long organizationId;
    private Long locationId;
    private LocalDate endCallDate;
    private LocalDate startDate;
    private LocalDate endDate;
}
