package com.travo.tcms.platform.dto;

public record BankAccountDto(
    String bankName,
    String branch,
    String ifsc,
    String accountNumber,
    String accountHolder,
    String cancelledCheque
) {}
