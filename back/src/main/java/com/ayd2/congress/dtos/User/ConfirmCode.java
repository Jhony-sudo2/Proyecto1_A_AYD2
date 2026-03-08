package com.ayd2.congress.dtos.User;

import lombok.Value;

@Value
public class ConfirmCode {
    private String email;
    private String code;
    private String newPassword;
}
