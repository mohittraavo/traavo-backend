package com.travo.tcms.platform.controller;

import com.travo.tcms.platform.entity.StaffMemberEntity;
import com.travo.tcms.platform.service.StaffMemberService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/staff")
public class StaffMemberController {
  private final StaffMemberService service;

  public StaffMemberController(StaffMemberService service) {
    this.service = service;
  }

  @GetMapping("/options")
  public Map<String, Object> options() {
    return Map.of(
        "workforces", StaffMemberService.WORKFORCES,
        "statuses", StaffMemberService.STATUSES,
        "employmentTypes", StaffMemberService.EMPLOYMENT_TYPES,
        "departments", StaffMemberService.DEPARTMENTS,
        "designations", StaffMemberService.DESIGNATIONS
    );
  }

  @GetMapping
  public List<StaffMemberEntity> list() {
    return service.list();
  }

  @PostMapping
  public StaffMemberEntity create(@RequestBody StaffMemberEntity body) {
    return service.create(body);
  }

  @PutMapping("/{id}")
  public StaffMemberEntity update(@PathVariable Long id, @RequestBody StaffMemberEntity body) {
    return service.update(id, body);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/template")
  public ResponseEntity<byte[]> template() throws IOException {
    byte[] data = new ClassPathResource("templates/Traavo_Staff_Bulk_Import_Template.csv").getContentAsByteArray();
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Traavo_Staff_Bulk_Import_Template.csv")
        .contentType(MediaType.parseMediaType("text/csv"))
        .body(data);
  }

  @GetMapping("/export")
  public ResponseEntity<byte[]> export() {
    byte[] data = service.exportCsv().getBytes(StandardCharsets.UTF_8);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=staff-directory.csv")
        .contentType(MediaType.parseMediaType("text/csv"))
        .body(data);
  }

  @PostMapping(value = "/bulk-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Map<String, Object>> bulkUpload(@RequestParam("file") MultipartFile file) throws IOException {
    if (file == null || file.isEmpty()) {
      return ResponseEntity.badRequest().body(Map.of("message", "A CSV file is required."));
    }
    String name = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
    if (!name.endsWith(".csv")) {
      return ResponseEntity.badRequest().body(Map.of("message", "Upload the staff template as a .csv file."));
    }
    return ResponseEntity.ok(service.importRows(new String(file.getBytes(), StandardCharsets.UTF_8)));
  }
}
