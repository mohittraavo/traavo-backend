package com.travo.tcms.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.travo.tcms.platform.entity.SchoolAddressEntity;

public interface SchoolAddressRepository extends JpaRepository<SchoolAddressEntity, Long> {
}
