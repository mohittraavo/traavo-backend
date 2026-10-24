package com.travo.tcms.platform.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.travo.tcms.platform.entity.SchoolLicenseAssignmentEntity;

public interface SchoolLicenseAssignmentRepository extends JpaRepository<SchoolLicenseAssignmentEntity, Long> {
  boolean existsByLicenseKeyIgnoreCase(String licenseKey);
  boolean existsByTenantIdIgnoreCase(String tenantId);
  boolean existsBySubscriptionNumberIgnoreCase(String subscriptionNumber);
  boolean existsBySchoolExternalIdAndStatusIgnoreCase(String schoolExternalId, String status);
  boolean existsBySchoolExternalIdAndLicenseCodeAndStatusIgnoreCase(String schoolExternalId, String licenseCode, String status);
  List<SchoolLicenseAssignmentEntity> findAllByOrderByAssignedDateDesc();
  List<SchoolLicenseAssignmentEntity> findByStatusIgnoreCase(String status);
}
