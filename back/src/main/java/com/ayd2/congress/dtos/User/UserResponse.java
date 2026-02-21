package com.ayd2.congress.dtos.User;

import com.ayd2.congress.models.User.UserEntity;

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
    private boolean isActive;
    private String nacionality;
    private Long rolId;
    private Long organizationId;

    public UserResponse(UserEntity userEntity) {
        this.id = userEntity.getId();
        this.identification = userEntity.getIdentification();
        this.name = userEntity.getName();
        this.lastName = userEntity.getLastName();
        this.email = userEntity.getEmail();
        this.phone = userEntity.getPhone();
        this.imageUrl = userEntity.getImageUrl();
        this.isActive = userEntity.isActive();
        this.nacionality = userEntity.getNacionality();
        this.rolId = userEntity.getRol().getId();
        this.organizationId = userEntity.getOrganization().getId();
    }
}
