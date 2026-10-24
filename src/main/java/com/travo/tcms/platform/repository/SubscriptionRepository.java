package com.travo.tcms.platform.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.travo.tcms.platform.entity.SubscriptionEntity;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {
  List<SubscriptionEntity> findAllByOrderByCreatedAtDesc();
  List<SubscriptionEntity> findBySchoolExternalIdOrderByCreatedAtDesc(String schoolExternalId);
  Optional<SubscriptionEntity> findFirstBySchoolExternalIdAndStatusIgnoreCase(String schoolExternalId, String status);
  Optional<SubscriptionEntity> findByExternalId(String externalId);
}
