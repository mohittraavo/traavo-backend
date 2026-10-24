package com.travo.tcms.platform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record InternalUserRequest(@NotBlank String name, @NotBlank String role, @NotBlank String department,
                                   @Email @NotBlank String email, @NotBlank String status) {
}
