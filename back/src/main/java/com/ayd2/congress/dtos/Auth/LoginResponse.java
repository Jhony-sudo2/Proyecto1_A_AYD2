package com.ayd2.congress.dtos.Auth;

import lombok.Value;

@Value
public class LoginResponse {
    String accesToken;
    String tokenType;
    long expiresIn;
}
