package com.ayd2.congress.dtos.User;

import lombok.Value;

@Value
public class UpdatePassword {
    private String currentPassword;
    private String newPassword;
}
