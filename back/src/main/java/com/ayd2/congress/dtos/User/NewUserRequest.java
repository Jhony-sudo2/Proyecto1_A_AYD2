package com.ayd2.congress.dtos.User;

import com.ayd2.congress.models.Organization.OrganizationEntity;
import com.ayd2.congress.models.User.RolEntity;
import com.ayd2.congress.models.User.UserEntity;

import lombok.Value;

@Value
public class NewUserRequest {
    String identification;
    String name;
    String lastName;
    String email;
    String phone;
    String imageUrl;
    String nacionality;
    Long rol;
    Long organization;
    String password;

    public UserEntity createEntity(){
        UserEntity newEntity = new UserEntity();
        newEntity.setName(name);
        newEntity.setLastName(lastName);
        newEntity.setIdentification(identification);
        newEntity.setEmail(email);
        newEntity.setImageUrl(imageUrl);
        newEntity.setRol(new RolEntity());
        newEntity.setOrganization(new OrganizationEntity());
        newEntity.setPassword(password);
        return newEntity; 
    }

}
