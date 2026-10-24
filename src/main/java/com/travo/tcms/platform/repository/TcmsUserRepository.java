package com.travo.tcms.platform.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.travo.tcms.platform.entity.TcmsUserEntity;

public interface TcmsUserRepository extends JpaRepository<TcmsUserEntity, Long> {
  Optional<TcmsUserEntity> findByEmailIgnoreCase(String email);
  Optional<TcmsUserEntity> findByExternalId(String externalId);
  List<TcmsUserEntity> findByUserTypeOrderByIdAsc(String userType);

  long countByUserType(String userType);
}
