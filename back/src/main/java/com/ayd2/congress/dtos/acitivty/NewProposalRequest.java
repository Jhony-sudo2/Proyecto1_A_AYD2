package com.ayd2.congress.dtos.acitivty;

import com.ayd2.congress.models.Enums.ActivityType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class NewProposalRequest {

    @NotNull(message = "Congress is required")
    private Long congressId;

    @NotNull(message = "User is required")
    private Long userId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Activity type is required")
    private ActivityType type;
}