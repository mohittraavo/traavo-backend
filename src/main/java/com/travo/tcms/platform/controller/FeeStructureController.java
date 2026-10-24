package com.travo.tcms.platform.controller;

import com.travo.tcms.platform.entity.FeeStructureEntity;
import com.travo.tcms.platform.service.FeeStructureService;
import org.springframework.http.HttpStatus;
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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fee-structures")
public class FeeStructureController {
  private final FeeStructureService service;

  public FeeStructureController(FeeStructureService service) {
    this.service = service;
  }

  @GetMapping
  public List<FeeStructureEntity> list(@RequestParam Map<String, String> filters) {
    return service.list(filters);
  }

  @GetMapping("/{id}")
  public FeeStructureEntity get(@PathVariable String id) {
    return service.get(id);
  }

  @PostMapping
  public ResponseEntity<FeeStructureEntity> create(@RequestBody FeeStructureEntity structure) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.create(structure));
  }

  @PutMapping("/{id}")
  public FeeStructureEntity update(@PathVariable String id, @RequestBody FeeStructureEntity structure) {
    return service.update(id, structure);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}
