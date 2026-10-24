package com.travo.tcms.platform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserCreateRequest(Long tenantId, @NotBlank String fullName, @Email @NotBlank String email,
                                 @NotBlank String role, @NotBlank String password) {
}
