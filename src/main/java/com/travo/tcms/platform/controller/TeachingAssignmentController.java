package com.travo.tcms.platform.controller;

import com.travo.tcms.platform.dto.TeachingRosterRow;
import com.travo.tcms.platform.entity.TeachingAssignmentEntity;
import com.travo.tcms.platform.service.TeachingAssignmentService;
import java.util.List;
import java.util.Map;
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
@RequestMapping("/api/teaching-assignments")
public class TeachingAssignmentController {
  private final TeachingAssignmentService service;

  public TeachingAssignmentController(TeachingAssignmentService service) {
    this.service = service;
  }

  @GetMapping
  public List<TeachingAssignmentEntity> list() {
    return service.list();
  }

  @GetMapping("/roster")
  public List<TeachingRosterRow> roster() {
    return service.roster();
  }

  @GetMapping("/options")
  public Map<String, Object> options() {
    return Map.of("dutyTypes", TeachingAssignmentService.DUTY_TYPES);
  }

  @PostMapping
  public ResponseEntity<?> create(@RequestBody TeachingAssignmentEntity body) {
    try {
      return ResponseEntity.ok(service.create(body));
    } catch (IllegalArgumentException exception) {
      return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> update(@PathVariable Long id, @RequestBody TeachingAssignmentEntity body) {
    try {
      return ResponseEntity.ok(service.update(id, body));
    } catch (IllegalArgumentException exception) {
      return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable Long id) {
    try {
      service.delete(id);
      return ResponseEntity.noContent().build();
    } catch (IllegalArgumentException exception) {
      return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
    }
  }
}
