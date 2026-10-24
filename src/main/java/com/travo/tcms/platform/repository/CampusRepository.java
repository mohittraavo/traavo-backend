package com.travo.tcms.platform.repository;

import com.travo.tcms.platform.entity.CampusEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampusRepository extends JpaRepository<CampusEntity, Long> {
  List<CampusEntity> findAllByOrderByCampusNameAsc();

  Optional<CampusEntity> findByExternalId(String externalId);
}
