package com.travo.tcms.platform.dto;

import jakarta.validation.constraints.NotBlank;

public record RolePermissionItemRequest(@NotBlank String name, boolean enabled) {
}
