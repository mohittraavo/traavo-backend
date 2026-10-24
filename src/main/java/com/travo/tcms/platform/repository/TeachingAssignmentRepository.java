package com.travo.tcms.platform.repository;

import com.travo.tcms.platform.entity.TeachingAssignmentEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeachingAssignmentRepository extends JpaRepository<TeachingAssignmentEntity, Long> {
  List<TeachingAssignmentEntity> findByStaffId(Long staffId);

  List<TeachingAssignmentEntity> findByAcademicYearAndClassNameIgnoreCaseAndSectionNameIgnoreCaseAndClassTeacherTrue(
      String academicYear,
      String className,
      String sectionName
  );
}
