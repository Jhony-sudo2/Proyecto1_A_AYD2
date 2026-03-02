package com.ayd2.congress.dtos.Inscription;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Value;

@Value
public class PayRequest {
    @NotNull(message = "user Id required")
    @Positive
    private Long userId;
    @NotNull(message = "user Id required")
    @Positive
    private Long congressId;
    @NotNull(message = "date is required")
    private LocalDate date;
}
