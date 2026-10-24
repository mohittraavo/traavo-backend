package com.travo.tcms.platform.repository;

import com.travo.tcms.platform.entity.AcademicClassEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcademicClassRepository extends JpaRepository<AcademicClassEntity, Long> {
  List<AcademicClassEntity> findAllByOrderByDisplayOrderAscNameAsc();

  boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);

  boolean existsByCodeIgnoreCase(String code);

  Optional<AcademicClassEntity> findFirstByNameIgnoreCase(String name);
}
