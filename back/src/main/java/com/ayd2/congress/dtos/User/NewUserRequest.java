package com.ayd2.congress.dtos.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
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
    @NotNull(message = "Rol is required")
    @Positive(message = "rol must be positive")
    private Long rol;
    @NotNull(message = "Organization required")
    @Positive(message = "organization must be positive")
    private Long organization;
    @NotBlank(message = "Password required")
    @Size(min = 8,max = 50,message = "Password invalid")
    private String password;
}
