package com.travo.tcms.platform.repository;

import com.travo.tcms.platform.entity.SchoolUserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolUserRepository extends JpaRepository<SchoolUserEntity, Long> {
  Optional<SchoolUserEntity> findByEmailIgnoreCase(String email);
}
