package com.ayd2.congress.dtos.User;
import lombok.Value;

@Value
public class UserResponse {
    private Long id;
    private String identification;
    private String name;
    private String lastName;
    private String email;
    private String phone;
    private String imageUrl;
    private boolean active;
    private String nacionality;
    private Long rolId;
    private Long organizationId;
}
