package com.example.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
        @NotBlank
        @Size(max = 50)
        String username,

        @NotBlank
        @Size(min = 6, max = 100)
        String password,

        @Size(max = 50)
        String nickname,

        @Email
        @Size(max = 100)
        String email
) {
}
