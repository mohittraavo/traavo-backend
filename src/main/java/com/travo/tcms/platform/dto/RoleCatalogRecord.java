package com.travo.tcms.platform.dto;

import java.time.LocalDate;
import java.util.List;

public record RoleCatalogRecord(String id, String roleName, String description, int usersAssigned, int modulesAssigned,
                                 String createdBy, LocalDate createdDate, String status, String type,
                                 List<RoleModulePermission> permissions) {
}
