package com.travo.tcms.platform.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.travo.tcms.platform.entity.SchoolEntity;

import java.util.Optional;
import java.util.List;

public interface SchoolRepository extends JpaRepository<SchoolEntity, Long> {
  Optional<SchoolEntity> findByExternalId(String externalId);

  List<SchoolEntity> findAllByOrderByCreatedDateDesc();
}
