package com.travo.tcms.platform.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.travo.tcms.platform.entity.LicenseEntity;

public interface LicenseRepository extends JpaRepository<LicenseEntity, Long> {
  List<LicenseEntity> findAllByOrderByCreatedAtDesc();

  long countByStatus(String status);
}
