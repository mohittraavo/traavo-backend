package com.travo.tcms.platform.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.travo.tcms.platform.entity.RolePermissionEntity;

public interface RolePermissionRepository extends JpaRepository<RolePermissionEntity, Long> {
  List<RolePermissionEntity> findByRoleIdOrderByModuleNameAscPermissionNameAsc(Long roleId);
  void deleteByRoleId(Long roleId);
}
