package com.travo.tcms.platform.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.travo.tcms.platform.entity.LicenseFeatureMappingEntity;

public interface LicenseFeatureMappingRepository extends JpaRepository<LicenseFeatureMappingEntity, Long> {
  Optional<LicenseFeatureMappingEntity> findByLicenseCodeIgnoreCaseAndFeatureNameIgnoreCase(String licenseCode, String featureName);
  List<LicenseFeatureMappingEntity> findByLicenseCodeIgnoreCaseOrderByFeatureNameAsc(String licenseCode);
}
