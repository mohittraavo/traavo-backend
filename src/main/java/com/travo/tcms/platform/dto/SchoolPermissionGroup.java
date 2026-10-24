package com.travo.tcms.platform.dto;

import java.util.List;

public record SchoolPermissionGroup(String module, List<RolePermissionItem> permissions) {
}
