package com.travo.tcms.platform.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tcms_school_bank_accounts")
public class SchoolBankAccountEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public String bankName;
  public String accountNumber;
  public String ifsc;
  public String branch;
  public String accountHolderName;
}
