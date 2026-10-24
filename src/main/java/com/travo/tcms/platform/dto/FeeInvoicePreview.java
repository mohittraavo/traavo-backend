package com.travo.tcms.platform.dto;

import java.math.BigDecimal;

public class FeeInvoicePreview {
  public Long studentId;
  public String studentName;
  public String classGrade;
  public String gradeLabel;
  public String admissionNo;
  public String enrollmentId;
  public String feeStructureId;
  public String feeStructureName;
  public String effectiveDate;
  public BigDecimal baseAmount;
  public BigDecimal concession;
  public BigDecimal lateFine;
  public String suggestedDueDate;
}
