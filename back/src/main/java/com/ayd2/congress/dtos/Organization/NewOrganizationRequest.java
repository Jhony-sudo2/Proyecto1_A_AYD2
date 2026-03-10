package com.ayd2.congress.dtos.Organization;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class NewOrganizationRequest {
    @NotBlank(message = "name is required")
    private String name;
    @NotBlank(message = "image is required")
    private String image;
}
