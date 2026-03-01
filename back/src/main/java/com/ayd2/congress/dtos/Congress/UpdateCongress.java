package com.ayd2.congress.dtos.Congress;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Value;

@Value
public class UpdateCongress {
    private String name;
    private String description;
    private LocalDateTime endCallDate;
    private String imageUrl;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long locationId;
}
