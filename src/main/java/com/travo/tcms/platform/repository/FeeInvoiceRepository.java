package com.travo.tcms.platform.repository;

import com.travo.tcms.platform.entity.FeeInvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeeInvoiceRepository extends JpaRepository<FeeInvoiceEntity, String> {
  long countByInvoiceNoStartingWith(String prefix);

  long countByReceiptNoStartingWith(String prefix);

  boolean existsByReceiptNo(String receiptNo);

  boolean existsByStudentIdAndFeePeriodAndStatusNot(Long studentId, String feePeriod, String status);
}
