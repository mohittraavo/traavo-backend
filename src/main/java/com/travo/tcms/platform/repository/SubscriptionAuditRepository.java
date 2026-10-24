package com.travo.tcms.platform.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.travo.tcms.platform.entity.SubscriptionAuditEntity;

public interface SubscriptionAuditRepository extends JpaRepository<SubscriptionAuditEntity, Long> {
  List<SubscriptionAuditEntity> findByEntityExternalIdOrderByCreatedAtDesc(String entityExternalId);
  List<SubscriptionAuditEntity> findAllByOrderByCreatedAtDesc();
}
