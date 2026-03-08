package com.ayd2.congress.dtos.Congress;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class NewCommitteeRequest {
    @NotBlank(message = "el usuario es requerido")
    private Long userId;
}
