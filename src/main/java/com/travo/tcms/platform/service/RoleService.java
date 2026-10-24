package com.travo.tcms.platform.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.travo.tcms.platform.repository.RolePermissionRepository;
import com.travo.tcms.platform.repository.RoleRepository;
import com.travo.tcms.platform.entity.RoleEntity;
import com.travo.tcms.platform.entity.RolePermissionEntity;
import com.travo.tcms.platform.dto.RoleCatalogRecord;
import com.travo.tcms.platform.dto.RoleCatalogRequest;
import com.travo.tcms.platform.dto.RoleModulePermissionRequest;
import com.travo.tcms.platform.dto.RolePermissionItemRequest;
import com.travo.tcms.platform.dto.RoleModulePermission;
import com.travo.tcms.platform.dto.RolePermissionItem;

@Service
public class RoleService {
  private final RoleRepository roleRepository;
  private final RolePermissionRepository rolePermissionRepository;

  public RoleService(RoleRepository roleRepository, RolePermissionRepository rolePermissionRepository) {
    this.roleRepository = roleRepository;
    this.rolePermissionRepository = rolePermissionRepository;
  }

  public List<String> listRoles() {
    return roleRepository.findAllByOrderByCreatedDateDesc().stream().map(role -> role.roleName).toList();
  }

  public List<RoleCatalogRecord> listRoleCatalog() {
    return roleRepository.findAllByOrderByCreatedDateDesc().stream().map(this::toRoleCatalogRecord).toList();
  }

  public RoleCatalogRecord createRole(RoleCatalogRequest request) {
    RoleEntity entity = new RoleEntity();
    entity.externalId = "ROL-" + String.format("%03d", roleRepository.findAll().size() + 1);
    entity.roleName = request.roleName();
    entity.description = request.description();
    entity.status = request.status();
    entity.type = "Custom";
    entity.usersAssigned = 0;
    entity.modulesAssigned = (int) request.permissions().stream().filter(module -> module.permissions().stream().anyMatch(RolePermissionItemRequest::enabled)).count();
    entity.createdBy = "Mohit Kumar";
    entity.createdDate = LocalDate.now();
    roleRepository.save(entity);
    replacePermissions(entity.id, request.permissions());
    return toRoleCatalogRecord(entity);
  }

  public RoleCatalogRecord updateRole(String externalId, RoleCatalogRequest request) {
    RoleEntity entity = roleRepository.findByExternalId(externalId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));
    entity.roleName = request.roleName();
    entity.description = request.description();
    entity.status = request.status();
    entity.modulesAssigned = (int) request.permissions().stream().filter(module -> module.permissions().stream().anyMatch(RolePermissionItemRequest::enabled)).count();
    roleRepository.save(entity);
    replacePermissions(entity.id, request.permissions());
    return toRoleCatalogRecord(entity);
  }

  public void deleteRole(String externalId) {
    RoleEntity entity = roleRepository.findByExternalId(externalId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));
    rolePermissionRepository.deleteByRoleId(entity.id);
    roleRepository.delete(entity);
  }

  public Map<String, Map<String, Boolean>> getRolePermissions() {
    Map<String, Map<String, Boolean>> payload = new LinkedHashMap<>();
    for (RoleCatalogRecord role : listRoleCatalog()) {
      Map<String, Boolean> permissions = new LinkedHashMap<>();
      for (RoleModulePermission module : role.permissions()) {
        for (RolePermissionItem permission : module.permissions()) {
          permissions.put(module.module() + "." + permission.name(), permission.enabled());
        }
      }
      payload.put(role.roleName(), permissions);
    }
    return payload;
  }

  public Map<String, Map<String, Boolean>> updatePermission(String roleName, String permissionKey, boolean enabled) {
    RoleEntity entity = roleRepository.findAll().stream()
        .filter(role -> role.roleName.equalsIgnoreCase(roleName))
        .findFirst()
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));

    List<RolePermissionEntity> permissions = rolePermissionRepository.findByRoleIdOrderByModuleNameAscPermissionNameAsc(entity.id);
    for (RolePermissionEntity permission : permissions) {
      String key = permission.moduleName + "." + permission.permissionName;
      if (key.equalsIgnoreCase(permissionKey)) {
        permission.enabled = enabled;
        rolePermissionRepository.save(permission);
        break;
      }
    }
    return getRolePermissions();
  }

  private void replacePermissions(Long roleId, List<RoleModulePermissionRequest> permissions) {
    rolePermissionRepository.deleteByRoleId(roleId);
    List<RolePermissionEntity> entities = new ArrayList<>();
    for (RoleModulePermissionRequest module : permissions) {
      for (RolePermissionItemRequest permission : module.permissions()) {
        RolePermissionEntity entity = new RolePermissionEntity();
        entity.roleId = roleId;
        entity.moduleName = module.module();
        entity.permissionName = permission.name();
        entity.enabled = permission.enabled();
        entities.add(entity);
      }
    }
    rolePermissionRepository.saveAll(entities);
  }

  private RoleCatalogRecord toRoleCatalogRecord(RoleEntity entity) {
    List<RoleModulePermission> modules = rolePermissionRepository.findByRoleIdOrderByModuleNameAscPermissionNameAsc(entity.id).stream()
        .collect(LinkedHashMap<String, List<RolePermissionEntity>>::new,
            (map, permission) -> map.computeIfAbsent(permission.moduleName, ignored -> new ArrayList<>()).add(permission),
            LinkedHashMap::putAll)
        .entrySet().stream()
        .map(entry -> new RoleModulePermission(entry.getKey(), entry.getValue().stream()
            .map(permission -> new RolePermissionItem(permission.permissionName, permission.enabled)).toList()))
        .toList();
    return new RoleCatalogRecord(entity.externalId, entity.roleName, entity.description, entity.usersAssigned,
        entity.modulesAssigned, entity.createdBy, entity.createdDate, entity.status, entity.type, modules);
  }
}
