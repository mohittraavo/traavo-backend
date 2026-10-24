package com.travo.tcms.platform.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.travo.tcms.platform.entity.SubscriptionPlanEntity;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlanEntity, Long> {
  Optional<SubscriptionPlanEntity> findByExternalId(String externalId);
  Optional<SubscriptionPlanEntity> findByCodeIgnoreCase(String code);
  List<SubscriptionPlanEntity> findAllByOrderByUpdatedAtDesc();
}
