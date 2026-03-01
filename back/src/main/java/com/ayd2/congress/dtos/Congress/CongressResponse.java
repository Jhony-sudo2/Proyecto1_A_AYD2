package com.ayd2.congress.dtos.Congress;

import java.time.LocalDate;

import lombok.Value;

@Value
public class CongressResponse {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String imageUrl;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate endCallDate;
    private String organizationName;
    private String locationName;
}
