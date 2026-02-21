package com.ayd2.congress.dtos.User;

import com.ayd2.congress.models.Organization.OrganizationEntity;
import com.ayd2.congress.models.User.RolEntity;
import com.ayd2.congress.models.User.UserEntity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class NewUserRequest {
    @NotBlank(message = "identifcation required")
    private String identification;
    @NotBlank(message = "name required")
    private String name;
    @NotBlank(message = "Last Name required")
    private String lastName;
    @NotBlank(message = "Email required")
    @Email(message = "Email invalid")
    private String email;
    @NotBlank(message = "identifcation required")
    @Pattern(regexp = "^[0-9+\\- ]{7,20}$", message = "Phone invalid")
    private String phone;
    @NotBlank(message = "Image is required")
    private String imageUrl;
    @NotBlank(message = "nacionality requuired")
    private String nacionality;
    private Long rol;
    @NotBlank(message = "Organization required")
    private Long organization;
    @NotBlank(message = "Password required")
    @Size(min = 8,max = 50,message = "Password invalid")
    private String password;

    public UserEntity createEntity(RolEntity rol,OrganizationEntity organizationEntity,String hashPassword){
        UserEntity newEntity = new UserEntity();
        newEntity.setName(this.name);
        newEntity.setLastName(this.lastName);
        newEntity.setIdentification(this.identification);
        newEntity.setEmail(this.email);
        newEntity.setImageUrl(this.imageUrl);
        newEntity.setRol(rol);
        newEntity.setOrganization(organizationEntity);
        newEntity.setPassword(hashPassword);
        newEntity.setNacionality(this.nacionality);
        newEntity.setPhone(this.phone);
        return newEntity; 
    }

}
