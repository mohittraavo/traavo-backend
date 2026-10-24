package com.travo.tcms.platform.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.travo.tcms.platform.entity.LicenseModuleMappingEntity;

public interface LicenseModuleMappingRepository extends JpaRepository<LicenseModuleMappingEntity, Long> {
  Optional<LicenseModuleMappingEntity> findByLicenseCodeIgnoreCaseAndModuleNameIgnoreCase(String licenseCode, String moduleName);
  List<LicenseModuleMappingEntity> findByLicenseCodeIgnoreCaseOrderByModuleNameAsc(String licenseCode);
}
