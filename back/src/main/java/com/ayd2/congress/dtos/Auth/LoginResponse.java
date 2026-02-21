package com.ayd2.congress.dtos.Auth;

import lombok.Value;

@Value
public class LoginResponse {
    private Long userId;
    private String token;
}
