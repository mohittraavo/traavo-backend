package com.travo.tcms.platform.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.travo.tcms.platform.entity.LicenseMasterEntity;

public interface LicenseMasterRepository extends JpaRepository<LicenseMasterEntity, Long> {
  Optional<LicenseMasterEntity> findByLicenseCodeIgnoreCase(String licenseCode);
  boolean existsByLicenseCodeIgnoreCase(String licenseCode);
  boolean existsByLicenseNameIgnoreCase(String licenseName);
  List<LicenseMasterEntity> findAllByOrderByCreatedAtDesc();
}
