package com.ayd2.congress.dtos.Congress;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Value;

@Value
public class NewCongressRequest {
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Description is required")
    private String description;
    @NotNull(message = "Price is required")
    @Positive(message = "El precio tiene que ser positov")
    private Double price;
    @NotNull(message = "Organization id is required")
    private Long organizationId;
    @NotNull(message = "Location id is required")
    private Long locationId;
    @NotNull(message = "Start date is required")
    private LocalDateTime endCallDate;
    @NotNull(message = "End date is required")
    private LocalDateTime startDate;
    @NotNull(message = "End date is required")
    private LocalDateTime endDate;
    @NotBlank(message = "Image is required")
    private String imageUrl;
}
