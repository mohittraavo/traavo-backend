package com.travo.tcms.platform.repository;

import com.travo.tcms.platform.entity.AdmissionApplicationEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdmissionApplicationRepository extends JpaRepository<AdmissionApplicationEntity, Long> {
  List<AdmissionApplicationEntity> findAllByOrderByApplicationNoDesc();

  boolean existsByApplicationNoIgnoreCase(String applicationNo);
}
