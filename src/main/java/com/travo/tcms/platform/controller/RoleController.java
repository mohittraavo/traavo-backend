package com.travo.tcms.platform.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.travo.tcms.platform.dto.*;
import com.travo.tcms.platform.service.RoleService;

@RestController
@RequestMapping("/admin")
public class RoleController {
  private final RoleService roleService;

  public RoleController(RoleService roleService) {
    this.roleService = roleService;
  }

  @GetMapping("/roles")
  public List<String> listRoles() {
    return roleService.listRoles();
  }

  @GetMapping("/roles/catalog")
  public List<RoleCatalogRecord> listRoleCatalog() {
    return roleService.listRoleCatalog();
  }

  @PostMapping("/roles/catalog")
  public RoleCatalogRecord createRole(@Valid @RequestBody RoleCatalogRequest request) {
    return roleService.createRole(request);
  }

  @PutMapping("/roles/catalog/{externalId}")
  public RoleCatalogRecord updateRole(@PathVariable String externalId,
                                      @Valid @RequestBody RoleCatalogRequest request) {
    return roleService.updateRole(externalId, request);
  }

  @DeleteMapping("/roles/catalog/{externalId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteRole(@PathVariable String externalId) {
    roleService.deleteRole(externalId);
  }

  @GetMapping("/permissions")
  public Map<String, Map<String, Boolean>> getRolePermissions(){
    return roleService.getRolePermissions();
  }

  @PutMapping("/permissions/{role}/{permissionKey}")
  public Map<String, Map<String, Boolean>> updatePermission(@PathVariable String role,
                                                            @PathVariable String permissionKey,
                                                            @Valid @RequestBody PermissionToggleRequest request) {
    return roleService.updatePermission(role, permissionKey, request.enabled());
  }
}
