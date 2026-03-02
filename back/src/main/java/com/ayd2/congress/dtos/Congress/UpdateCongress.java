package com.ayd2.congress.dtos.Congress;

import java.time.LocalDate;

import lombok.Value;

@Value
public class UpdateCongress {
    private String name;
    private String description;
    private LocalDate endCallDate;
    private String imageUrl;
    private Long locationId;
}
