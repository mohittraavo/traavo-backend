package com.travo.tcms.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.travo.tcms.platform.entity.SchoolContactEntity;

public interface SchoolContactRepository extends JpaRepository<SchoolContactEntity, Long> {
}
