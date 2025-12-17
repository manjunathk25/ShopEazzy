package com.shopEZ.ShopEazzy.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class SignUpRequest {

    @NotBlank(message = "User name must not be blank.")
    @Size(min = 3, max = 20, message = "User name must contain minimum 3 and maximum 20 characters.")
    private String userName;

    @NotBlank(message = "Email must not be blank.")
    @Size(min = 3, max = 50, message = "Email must contain minimum 3 and maximum 50 characters.")
    @Email(message = "Email must be valid.")
    private String email;

    @NotBlank(message = "Password must not be blank.")
    @Size(min = 6, max = 60, message = "Password must contain minimum 6 and maximum 60 characters.")
    private String password;

    private Set<String> roles;
}
