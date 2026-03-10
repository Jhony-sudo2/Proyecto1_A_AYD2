package com.ayd2.congress.dtos.User;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class UpdatePassword {
    @NotBlank(message = "current password is required")
    private String currentPassword;
    @NotBlank(message = "new password is required")
    private String newPassword;
}
