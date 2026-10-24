package com.travo.tcms.platform.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record RoleCatalogRequest(@NotBlank String roleName, String description, @NotBlank String status,
                                  @NotEmpty @Valid List<RoleModulePermissionRequest> permissions) {
}
