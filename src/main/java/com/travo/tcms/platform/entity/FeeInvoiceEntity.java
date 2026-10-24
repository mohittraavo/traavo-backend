package com.travo.tcms.platform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "tcms_fee_invoices", indexes = {
    @Index(name = "idx_fee_invoices_student", columnList = "student_id"),
    @Index(name = "idx_fee_invoices_status", columnList = "status")
})
public class FeeInvoiceEntity {
  @Id
  public String id;

  public String invoiceNo;
  public Long studentId;
  public String studentName;
  public String classGrade;
  public String gradeLabel;
  public String admissionNo;
  public String enrollmentId;
  public String feeStructureId;
  public String feeStructureName;
  public String effectiveDate;
  public String feePeriod;
  public String dueDate;

  @Column(precision = 14, scale = 2)
  public BigDecimal baseAmount;

  @Column(precision = 14, scale = 2)
  public BigDecimal concession;

  @Column(precision = 14, scale = 2)
  public BigDecimal lateFine;

  @Column(precision = 14, scale = 2)
  public BigDecimal totalBill;

  @Column(precision = 14, scale = 2)
  public BigDecimal amountPaid;

  @Column(precision = 14, scale = 2)
  public BigDecimal lastSettlementAmount;

  public String receiptNo;
  public String paymentDate;
  public String paymentMethod;
  public String referenceNo;
  public String remarks;
  public String status;
  public String createdDate;

  public FeeInvoiceEntity() {
  }
}
