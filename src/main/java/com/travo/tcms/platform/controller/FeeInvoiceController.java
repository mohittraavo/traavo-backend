package com.travo.tcms.platform.controller;

import com.travo.tcms.platform.dto.FeeInvoicePreview;
import com.travo.tcms.platform.dto.IssueInvoiceRequest;
import com.travo.tcms.platform.dto.RecordPaymentRequest;
import com.travo.tcms.platform.entity.FeeInvoiceEntity;
import com.travo.tcms.platform.service.FeeInvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/fee-invoices")
public class FeeInvoiceController {
  private final FeeInvoiceService service;

  public FeeInvoiceController(FeeInvoiceService service) {
    this.service = service;
  }

  @GetMapping
  public List<FeeInvoiceEntity> list(@RequestParam(required = false) String q, @RequestParam(required = false) String status) {
    return service.list(q, status);
  }

  @GetMapping("/preview")
  public FeeInvoicePreview preview(@RequestParam Long studentId) {
    return service.preview(studentId);
  }

  @PostMapping
  public ResponseEntity<FeeInvoiceEntity> issue(@RequestBody IssueInvoiceRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.issue(request));
  }

  @PostMapping("/{id}/payments")
  public FeeInvoiceEntity pay(@PathVariable String id, @RequestBody RecordPaymentRequest request) {
    return service.recordPayment(id, request);
  }
}
