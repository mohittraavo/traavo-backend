package com.travo.tcms.platform.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record RoleModulePermissionRequest(@NotBlank String module,
                                           @NotEmpty @Valid List<RolePermissionItemRequest> permissions) {
}
