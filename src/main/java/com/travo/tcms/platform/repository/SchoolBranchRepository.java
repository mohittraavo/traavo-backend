package com.travo.tcms.platform.repository;

import com.travo.tcms.platform.entity.SchoolBranchEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolBranchRepository extends JpaRepository<SchoolBranchEntity, Long> {
  List<SchoolBranchEntity> findAllByOrderByMainCampusDescNameAsc();

  boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);

  boolean existsByCodeIgnoreCase(String code);

  List<SchoolBranchEntity> findByMainCampusTrue();
}
