package com.travo.tcms.platform.controller;

import com.travo.tcms.platform.entity.StudentEntity;
import com.travo.tcms.platform.service.StudentService;
import com.travo.tcms.platform.util.StudentExcelHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {
  @Autowired
  private StudentService service;

  @GetMapping
  public List<StudentEntity> list(@RequestParam Map<String, String> all) {
    return service.list(all);
  }

  @GetMapping("/{id:\\d+}")
  public ResponseEntity<StudentEntity> get(@PathVariable Long id) {
    return service.get(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public StudentEntity create(@RequestBody StudentEntity student) {
    return service.create(student);
  }

  @PutMapping("/{id:\\d+}")
  public ResponseEntity<StudentEntity> update(@PathVariable Long id, @RequestBody StudentEntity student) {
    return ResponseEntity.ok(service.update(id, student));
  }

  @DeleteMapping("/{id:\\d+}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping(value = "/bulk-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Map<String, Object>> bulkUpload(@RequestParam("file") MultipartFile file) throws IOException {
    if (file == null || file.isEmpty()) {
      return ResponseEntity.badRequest().body(Map.of("message", "Excel file is required"));
    }
    String name = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
    if (!name.endsWith(".xlsx") && !name.endsWith(".xls") && !name.endsWith(".csv")) {
      return ResponseEntity.badRequest().body(Map.of("message", "Only .xlsx, .xls or .csv files are supported"));
    }

    var rows = StudentExcelHelper.parseExcel(file);
    var validation = StudentExcelHelper.validateRows(rows, service.existingAdmissionNos());
    var saved = service.bulkSave(validation.valid);

    List<Map<String, Object>> errs = new ArrayList<>();
    for (var error : validation.errors) {
      Map<String, Object> item = new HashMap<>();
      item.put("row", error.row);
      item.put("admissionNo", error.admissionNo);
      item.put("messages", error.messages);
      errs.add(item);
    }

    Map<String, Object> body = new HashMap<>();
    body.put("imported", saved.size());
    body.put("failed", errs.size());
    body.put("total", rows.size());
    body.put("errors", errs);
    return ResponseEntity.ok(body);
  }

  @GetMapping("/template")
  public ResponseEntity<byte[]> template() throws IOException {
    byte[] data = StudentExcelHelper.generateTemplate();
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students-template.xlsx")
        .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .body(data);
  }

  @GetMapping("/export")
  public ResponseEntity<byte[]> export(@RequestParam Map<String, String> all) throws IOException {
    var students = service.list(all);
    byte[] data = StudentExcelHelper.writeExcel(students);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students-export.xlsx")
        .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .body(data);
  }
}
