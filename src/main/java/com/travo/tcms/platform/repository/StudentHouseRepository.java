package com.travo.tcms.platform.repository;

import com.travo.tcms.platform.entity.StudentHouseEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentHouseRepository extends JpaRepository<StudentHouseEntity, Long> {
  List<StudentHouseEntity> findAllByOrderByNameAsc();

  boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);

  boolean existsByCodeIgnoreCase(String code);
}
