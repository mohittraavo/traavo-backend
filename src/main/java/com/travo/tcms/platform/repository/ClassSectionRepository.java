package com.travo.tcms.platform.repository;

import com.travo.tcms.platform.entity.ClassSectionEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassSectionRepository extends JpaRepository<ClassSectionEntity, Long> {
  List<ClassSectionEntity> findAllByOrderByNameAsc();

  boolean existsByClassId(Long classId);

  boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);

  boolean existsByCodeIgnoreCase(String code);

  List<ClassSectionEntity> findByClassIdAndNameIgnoreCase(Long classId, String name);
}
