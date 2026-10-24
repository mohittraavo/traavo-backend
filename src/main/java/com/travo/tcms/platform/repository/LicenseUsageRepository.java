package com.travo.tcms.platform.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.travo.tcms.platform.entity.LicenseUsageEntity;

public interface LicenseUsageRepository extends JpaRepository<LicenseUsageEntity, Long> {
  Optional<LicenseUsageEntity> findByAssignmentId(Long assignmentId);
}
