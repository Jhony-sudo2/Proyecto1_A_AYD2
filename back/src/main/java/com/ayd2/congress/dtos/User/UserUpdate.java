package com.ayd2.congress.dtos.User;

import com.ayd2.congress.models.User.UserEntity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Value;

@Value
public class UserUpdate {
    @NotBlank(message = "name is required")
    private String name;
    @NotBlank(message = "email is required")
    @Email(message = "email invalid")
    private String email;
    @NotBlank(message = "Last name is required")
    private String lastName;
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[0-9+\\- ]{7,20}$", message = "Phone invalid")
    private String phone;
    @NotBlank(message = "Profile photo is required")
    private String image;

    public UserEntity updateUser(UserEntity userToUpdate){
        userToUpdate.setName(name);
        userToUpdate.setLastName(lastName);
        userToUpdate.setEmail(email);
        userToUpdate.setImageUrl(image);
        userToUpdate.setPhone(phone);
        return userToUpdate;
    }
}
