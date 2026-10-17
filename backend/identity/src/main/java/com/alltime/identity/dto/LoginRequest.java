package com.alltime.identity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "Phone cant be blank")
    private String phoneNumber;

    @NotBlank(message = "Password cant be blank")
    @Size(min = 8, max = 12, message = "Password can be of length 8 to 12 characters")
    private String password;

}
