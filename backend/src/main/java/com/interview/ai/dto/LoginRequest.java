package com.interview.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Used for both login and registration.
 * For registration, email and fullName should also be supplied.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    // Only required for registration
    private String email;
    private String fullName;
}
