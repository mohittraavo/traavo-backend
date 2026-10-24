package com.travo.tcms.platform.repository;

import com.travo.tcms.platform.entity.StaffMemberEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StaffMemberRepository extends JpaRepository<StaffMemberEntity, Long> {
  boolean existsByEmployeeCodeIgnoreCase(String employeeCode);

  boolean existsByEmployeeCodeIgnoreCaseAndIdNot(String employeeCode, Long id);

  Optional<StaffMemberEntity> findByEmployeeCodeIgnoreCase(String employeeCode);

  @Query("select s.employeeCode from StaffMemberEntity s where s.employeeCode is not null and s.employeeCode <> ''")
  List<String> findEmployeeCodes();
}
