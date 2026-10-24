package com.travo.tcms.platform.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.travo.tcms.platform.entity.FeatureEntity;

public interface FeatureRepository extends JpaRepository<FeatureEntity, Long> {
  Optional<FeatureEntity> findByExternalId(String externalId);
  List<FeatureEntity> findAllByOrderByNameAsc();
}
