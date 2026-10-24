package com.travo.tcms.platform.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.travo.tcms.platform.entity.SessionEntity;

public interface SessionRepository extends JpaRepository<SessionEntity, Long> {
  Optional<SessionEntity> findByToken(String token);
  void deleteByToken(String token);
}
