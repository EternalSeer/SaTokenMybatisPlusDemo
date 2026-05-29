package com.example.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserEmailUpdateRequest(
        @NotBlank
        @Email
        String email
) {
}
