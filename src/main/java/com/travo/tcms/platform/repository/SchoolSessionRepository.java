package com.travo.tcms.platform.repository;

import com.travo.tcms.platform.entity.SchoolSessionEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolSessionRepository extends JpaRepository<SchoolSessionEntity, Long> {
  Optional<SchoolSessionEntity> findByToken(String token);
  void deleteByToken(String token);
}
