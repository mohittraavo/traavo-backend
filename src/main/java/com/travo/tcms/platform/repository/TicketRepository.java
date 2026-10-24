package com.travo.tcms.platform.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.travo.tcms.platform.entity.TicketEntity;

public interface TicketRepository extends JpaRepository<TicketEntity, Long> {
  List<TicketEntity> findAllByOrderByCreatedAtDesc();

  long countByStatusIn(Collection<String> statuses);
}
