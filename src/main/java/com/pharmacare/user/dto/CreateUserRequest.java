package com.pharmacare.user.dto;

import com.pharmacare.user.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotNull
    private Role role;

    private Boolean active;
}
