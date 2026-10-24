package com.travo.tcms.platform.controller;

import com.travo.tcms.platform.entity.AdmissionApplicationEntity;
import com.travo.tcms.platform.service.AdmissionApplicationService;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/admissions")
public class AdmissionApplicationController {
  private final AdmissionApplicationService service;

  public AdmissionApplicationController(AdmissionApplicationService service) {
    this.service = service;
  }

  @GetMapping
  public List<AdmissionApplicationEntity> list() {
    return service.list();
  }

  @GetMapping("/options")
  public Map<String, Object> options() {
    return service.options();
  }

  @GetMapping("/summary")
  public Map<String, Integer> summary() {
    return service.summary();
  }

  @PostMapping
  public AdmissionApplicationEntity create(@RequestBody AdmissionApplicationEntity body) {
    body.id = null;
    return service.save(body, false);
  }

  @PostMapping("/draft")
  public AdmissionApplicationEntity draft(@RequestBody AdmissionApplicationEntity body) {
    return service.save(body, true);
  }

  @PutMapping("/{id}")
  public AdmissionApplicationEntity update(@PathVariable Long id, @RequestBody AdmissionApplicationEntity body) {
    body.id = id;
    return service.save(body, false);
  }

  @PostMapping("/{id}/review")
  public AdmissionApplicationEntity review(@PathVariable Long id, @RequestBody Map<String, String> body) {
    return service.review(id, body.get("stage"), body.get("entranceScore"));
  }

  @PostMapping("/{id}/convert")
  public AdmissionApplicationEntity convert(@PathVariable Long id) {
    return service.convert(id);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/export")
  public ResponseEntity<byte[]> export() {
    byte[] data = service.exportCsv().getBytes(StandardCharsets.UTF_8);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=admissions-pipeline.csv")
        .contentType(MediaType.parseMediaType("text/csv"))
        .body(data);
  }
}
