package com.travo.tcms.platform.repository;

import com.travo.tcms.platform.entity.StudentEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
  boolean existsByAdmissionNoIgnoreCase(String admissionNo);

  @Query("select s.admissionNo from StudentEntity s where s.admissionNo is not null and s.admissionNo <> ''")
  List<String> findAdmissionNumbers();
}
