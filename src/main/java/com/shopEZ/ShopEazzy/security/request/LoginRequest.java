package com.shopEZ.ShopEazzy.security.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "User name must not be blank.")
    private String userName;

    @NotBlank(message = "Password must not be blank.")
    private String password;
}
