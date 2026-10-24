package com.travo.tcms.platform.repository;

import com.travo.tcms.platform.entity.StaffDepartmentEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffDepartmentRepository extends JpaRepository<StaffDepartmentEntity, Long> {
  List<StaffDepartmentEntity> findAllByOrderByIdAsc();

  boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);

  boolean existsByCodeIgnoreCase(String code);
}
