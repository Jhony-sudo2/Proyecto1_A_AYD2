package com.ayd2.congress.dtos.User;

import lombok.Value;

@Value
public class UserResponse {
    Long id;
    String identification;
    String name;
    String lastName;
    String email;
    String phone;
    String imageUrl;
    boolean isActive;
    String nacionality;
    Long rolId;
    Long organizationId;
}
