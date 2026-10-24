package com.travo.tcms.platform.repository;

import com.travo.tcms.platform.entity.FeeStructureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FeeStructureRepository extends JpaRepository<FeeStructureEntity, String> {
  @Query("""
      select count(f) > 0 from FeeStructureEntity f
      where lower(f.name) = lower(:name)
        and f.academicYear = :year
        and lower(f.campus) = lower(:campus)
        and f.id <> :id
      """)
  boolean existsDuplicate(@Param("name") String name, @Param("year") String year, @Param("campus") String campus, @Param("id") String id);
}
