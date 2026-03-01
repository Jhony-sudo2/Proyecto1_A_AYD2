package com.ayd2.congress.dtos.SysConfig;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Value;

@Value
public class SysConfigUpdate {
    @NotNull(message = "price is required")
    @Positive(message = "price must be positive")
    private Double price;
    @NotNull(message = "percentage is required")
    @Positive(message = "percentage must be positive")
    private Double percentage;
}
