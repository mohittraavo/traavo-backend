package com.travo.tcms.platform.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.travo.tcms.platform.dto.FeatureRecord;
import com.travo.tcms.platform.dto.FeatureRequest;
import com.travo.tcms.platform.service.FeatureService;

@RestController
@RequestMapping("/admin/features")
public class FeatureController {
  private final FeatureService featureService;

  public FeatureController(FeatureService featureService) {
    this.featureService = featureService;
  }

  @GetMapping
  public List<FeatureRecord> listFeatures() {
    return featureService.listFeatures();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public FeatureRecord createFeature(@Valid @RequestBody FeatureRequest request) {
    return featureService.createFeature(request);
  }

  @PutMapping("/{externalId}")
  public FeatureRecord updateFeature(@PathVariable String externalId, @Valid @RequestBody FeatureRequest request) {
    return featureService.updateFeature(externalId, request);
  }

  @DeleteMapping("/{externalId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteFeature(@PathVariable String externalId) {
    featureService.deleteFeature(externalId);
  }
}
