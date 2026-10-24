package com.travo.tcms.platform.service;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.travo.tcms.platform.dto.FeatureRecord;
import com.travo.tcms.platform.dto.FeatureRequest;
import com.travo.tcms.platform.entity.FeatureEntity;
import com.travo.tcms.platform.repository.FeatureRepository;

@Service
public class FeatureService {
  private final FeatureRepository featureRepository;

  public FeatureService(FeatureRepository featureRepository) {
    this.featureRepository = featureRepository;
  }

  public List<FeatureRecord> listFeatures() {
    return featureRepository.findAllByOrderByNameAsc().stream().map(this::toRecord).toList();
  }

  public FeatureRecord createFeature(FeatureRequest request) {
    FeatureEntity entity = new FeatureEntity();
    entity.externalId = nextExternalId();
    updateEntity(entity, request);
    return toRecord(featureRepository.save(entity));
  }

  public FeatureRecord updateFeature(String externalId, FeatureRequest request) {
    FeatureEntity entity = getFeature(externalId);
    updateEntity(entity, request);
    return toRecord(featureRepository.save(entity));
  }

  public void deleteFeature(String externalId) {
    featureRepository.delete(getFeature(externalId));
  }

  private FeatureEntity getFeature(String externalId) {
    return featureRepository.findByExternalId(externalId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Feature not found"));
  }

  private void updateEntity(FeatureEntity entity, FeatureRequest request) {
    entity.name = request.name().trim();
    entity.category = request.category().trim();
    entity.description = request.description() == null || request.description().isBlank()
        ? "Feature included in the subscription plan."
        : request.description().trim();
    entity.price = request.price();
    entity.billingCycle = request.billingCycle().trim().toUpperCase();
    entity.status = request.status().trim();
  }

  private String nextExternalId() {
    return "FEAT-" + String.format("%03d", featureRepository.count() + 1);
  }

  private FeatureRecord toRecord(FeatureEntity entity) {
    return new FeatureRecord(entity.externalId, entity.name, entity.category, entity.description,
        entity.price, entity.billingCycle, entity.status);
  }
}
