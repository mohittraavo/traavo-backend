package com.travo.tcms.platform.repository;

import com.travo.tcms.platform.entity.CurriculumSubjectEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurriculumSubjectRepository extends JpaRepository<CurriculumSubjectEntity, Long> {
  List<CurriculumSubjectEntity> findAllByOrderByDisplayOrderAscNameAsc();

  boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);

  boolean existsByCodeIgnoreCase(String code);
}
