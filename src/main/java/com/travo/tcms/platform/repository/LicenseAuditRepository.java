package com.travo.tcms.platform.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.travo.tcms.platform.entity.LicenseAuditEntity;

public interface LicenseAuditRepository extends JpaRepository<LicenseAuditEntity, Long> {
  List<LicenseAuditEntity> findByAssignmentIdOrderByCreatedAtDesc(Long assignmentId);
}
