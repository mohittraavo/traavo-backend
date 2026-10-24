package com.travo.tcms.platform.dto;

import java.util.List;

public record RoleModulePermission(String module, List<RolePermissionItem> permissions) {
}
