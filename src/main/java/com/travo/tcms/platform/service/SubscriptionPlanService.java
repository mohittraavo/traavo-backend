package com.travo.tcms.platform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travo.tcms.platform.entity.SubscriptionAuditEntity;
import com.travo.tcms.platform.entity.SubscriptionPlanEntity;
import com.travo.tcms.platform.repository.SubscriptionAuditRepository;
import com.travo.tcms.platform.repository.SubscriptionPlanRepository;
import com.travo.tcms.platform.repository.SubscriptionRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SubscriptionPlanService {
  private final SubscriptionPlanRepository planRepository;
  private final SubscriptionRepository subscriptionRepository;
  private final SubscriptionAuditRepository auditRepository;
  private final ObjectMapper objectMapper;

  public SubscriptionPlanService(SubscriptionPlanRepository planRepository,
      SubscriptionRepository subscriptionRepository, SubscriptionAuditRepository auditRepository,
      ObjectMapper objectMapper) {
    this.planRepository = planRepository;
    this.subscriptionRepository = subscriptionRepository;
    this.auditRepository = auditRepository;
    this.objectMapper = objectMapper;
  }

  public List<Map<String, Object>> listPlans() {
    return planRepository.findAllByOrderByUpdatedAtDesc().stream().map(this::toMap).toList();
  }

  public Map<String, Object> createPlan(Map<String, Object> payload) {
    validate(payload, false);
    String code = text(payload, "planCode", text(payload, "code", ""));
    if (planRepository.findByCodeIgnoreCase(code).isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Plan code already exists");
    }
    SubscriptionPlanEntity entity = new SubscriptionPlanEntity();
    entity.externalId = "PLAN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    apply(entity, payload);
    entity.createdAt = Instant.now();
    entity.updatedAt = entity.createdAt;
    entity.status = normalizeStatus(payload);
    if ("ACTIVE".equals(entity.status)) {
      validateActivation(entity);
    }
    planRepository.save(entity);
    audit(entity.externalId, "PLAN_CREATED", null, toMap(entity), null);
    return toMap(entity);
  }

  public Map<String, Object> updatePlan(String externalId, Map<String, Object> payload) {
    SubscriptionPlanEntity entity = getPlan(externalId);
    validate(payload, false);
    String code = text(payload, "planCode", text(payload, "code", entity.code));
    planRepository.findByCodeIgnoreCase(code).filter(existing -> !existing.id.equals(entity.id)).ifPresent(existing -> {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Plan code already exists");
    });
    Map<String, Object> oldValue = toMap(entity);
    apply(entity, payload);
    if (payload.containsKey("status")) {
      entity.status = normalizeStatus(payload);
    }
    if ("ACTIVE".equals(entity.status)) {
      validateActivation(entity);
    }
    entity.updatedAt = Instant.now();
    planRepository.save(entity);
    audit(entity.externalId, "PLAN_UPDATED", oldValue, toMap(entity), null);
    return toMap(entity);
  }

  public Map<String, Object> clonePlan(String externalId) {
    SubscriptionPlanEntity source = getPlan(externalId);
    SubscriptionPlanEntity clone = new SubscriptionPlanEntity();
    clone.externalId = "PLAN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    clone.code = "";
    clone.name = source.name + " Copy";
    clone.description = source.description;
    clone.type = source.type;
    clone.status = "DRAFT";
    clone.billingCycle = source.billingCycle;
    clone.price = source.price;
    clone.offerPrice = source.offerPrice;
    clone.currency = source.currency;
    clone.trialEnabled = source.trialEnabled;
    clone.trialDuration = source.trialDuration;
    clone.studentLimit = source.studentLimit;
    clone.campusLimit = source.campusLimit;
    clone.modulesJson = source.modulesJson;
    clone.featuresJson = source.featuresJson;
    clone.limitsJson = source.limitsJson;
    clone.createdAt = Instant.now();
    clone.updatedAt = clone.createdAt;
    planRepository.save(clone);
    audit(clone.externalId, "PLAN_CLONED", toMap(source), toMap(clone), null);
    return toMap(clone);
  }

  public Map<String, Object> changeStatus(String externalId, String status) {
    SubscriptionPlanEntity entity = getPlan(externalId);
    String normalized = status == null ? "" : status.trim().toUpperCase();
    if (!(normalized.equals("ACTIVE") || normalized.equals("INACTIVE") || normalized.equals("DRAFT"))) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid plan status");
    }
    if (normalized.equals("ACTIVE")) {
      validateActivation(entity);
    }
    String oldStatus = entity.status;
    entity.status = normalized;
    entity.updatedAt = Instant.now();
    planRepository.save(entity);
    audit(entity.externalId, "PLAN_" + normalized, Map.of("status", oldStatus), Map.of("status", normalized), null);
    return toMap(entity);
  }

  public SubscriptionPlanEntity getPlan(String externalIdOrCode) {
    String normalized = externalIdOrCode == null ? "" : externalIdOrCode.trim();
    return planRepository.findByExternalId(normalized)
        .or(() -> planRepository.findByCodeIgnoreCase(normalized))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found"));
  }

  private void validate(Map<String, Object> payload, boolean activation) {
    String name = text(payload, "planName", text(payload, "name", "")).trim();
    String code = text(payload, "planCode", text(payload, "code", "")).trim();
    if (name.isBlank() || name.length() > 100) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Plan name is required and must be at most 100 characters");
    if (code.isBlank() || code.length() > 50 || !code.matches("[A-Za-z0-9_-]+")) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Plan code must be alphanumeric and may contain _ or -");
    if (number(payload, "basePrice", number(payload, "price", 0)) < 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price cannot be negative");
    if (activation && listValues(payload.get("modules")).isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one module is required");
  }

  private void validateActivation(SubscriptionPlanEntity entity) {
    if (entity.price < 0 || isBlank(entity.billingCycle) || listJson(entity.modulesJson).isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Plan needs billing cycle, pricing, and at least one module before activation");
    }
  }

  private void apply(SubscriptionPlanEntity entity, Map<String, Object> payload) {
    entity.code = text(payload, "planCode", text(payload, "code", entity.code));
    entity.name = text(payload, "planName", text(payload, "name", entity.name));
    entity.description = text(payload, "description", entity.description);
    entity.type = text(payload, "type", entity.type == null ? "Custom" : entity.type);
    entity.billingCycle = text(payload, "billingCycle", entity.billingCycle == null ? "MONTHLY" : entity.billingCycle).toUpperCase();
    entity.price = number(payload, "basePrice", number(payload, "price", entity.price));
    entity.offerPrice = nullableNumber(payload, "offerPrice", entity.offerPrice);
    entity.currency = text(payload, "currency", entity.currency == null ? "INR" : entity.currency);
    entity.trialEnabled = bool(payload, "trialEnabled", bool(payload, "trial", entity.trialEnabled));
    entity.trialDuration = nullableNumber(payload, "trialDuration", nullableNumber(payload, "trialDays", entity.trialDuration));
    entity.studentLimit = number(payload, "maxStudents", number(payload, "studentLimit", entity.studentLimit));
    entity.campusLimit = number(payload, "maxSchoolsInTenant", number(payload, "campusLimit", entity.campusLimit));
    entity.modulesJson = json(listValues(payload.containsKey("modules") ? payload.get("modules") : payload.get("allowedModules"), listJson(entity.modulesJson)));
    entity.featuresJson = json(listValues(payload.get("features"), listJson(entity.featuresJson)));
    if (payload.containsKey("limits")) {
      entity.limitsJson = json(payload.get("limits"));
    }
  }

  private String normalizeStatus(Map<String, Object> payload) {
    String status = text(payload, "status", "");
    if (status.isBlank() && payload.containsKey("isActive")) return bool(payload, "isActive", true) ? "ACTIVE" : "DRAFT";
    return status.isBlank() ? "DRAFT" : status.toUpperCase();
  }

  private Map<String, Object> toMap(SubscriptionPlanEntity entity) {
    Map<String, Object> result = new LinkedHashMap<>();
    result.put("id", entity.externalId); result.put("code", entity.code); result.put("name", entity.name);
    result.put("planCode", entity.code); result.put("planName", entity.name); result.put("description", entity.description);
    result.put("type", entity.type); result.put("status", entity.status); result.put("price", entity.price);
    result.put("basePrice", entity.price); result.put("offerPrice", entity.offerPrice); result.put("currency", entity.currency);
    result.put("billingCycle", entity.billingCycle); result.put("trialEnabled", entity.trialEnabled);
    result.put("trialDuration", entity.trialDuration); result.put("studentLimit", entity.studentLimit);
    result.put("campusLimit", entity.campusLimit); result.put("modules", listJson(entity.modulesJson));
    result.put("features", listJson(entity.featuresJson)); result.put("limits", readJson(entity.limitsJson));
    result.put("updatedAt", entity.updatedAt); return result;
  }

  private void audit(String id, String action, Object oldValue, Object newValue, String schoolId) {
    SubscriptionAuditEntity audit = new SubscriptionAuditEntity();
    audit.entityType = "PLAN"; audit.entityExternalId = id; audit.action = action;
    audit.schoolExternalId = schoolId; audit.performedBy = "CURRENT_USER";
    audit.oldValue = json(oldValue); audit.newValue = json(newValue); audit.createdAt = Instant.now();
    auditRepository.save(audit);
  }

  private String text(Map<String, Object> map, String key, String fallback) { Object value = map == null ? null : map.get(key); return value == null ? fallback : String.valueOf(value); }
  private int number(Map<String, Object> map, String key, int fallback) { try { return map != null && map.get(key) != null && !String.valueOf(map.get(key)).isBlank() ? Integer.parseInt(String.valueOf(map.get(key))) : fallback; } catch (NumberFormatException e) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, key + " must be an integer"); } }
  private Integer nullableNumber(Map<String, Object> map, String key, Integer fallback) { Object value = map == null ? null : map.get(key); if (value == null || String.valueOf(value).isBlank()) return fallback; return number(map, key, 0); }
  private boolean bool(Map<String, Object> map, String key, boolean fallback) { Object value = map == null ? null : map.get(key); return value == null ? fallback : Boolean.parseBoolean(String.valueOf(value)); }
  private boolean isBlank(String value) { return value == null || value.isBlank(); }
  private List<Object> listValues(Object value) { return listValues(value, List.of()); }
  private List<Object> listValues(Object value, List<Object> fallback) { if (value instanceof List<?> list) return new ArrayList<>(list); return fallback == null ? new ArrayList<>() : new ArrayList<>(fallback); }
  private List<Object> listJson(String value) { if (value == null || value.isBlank()) return new ArrayList<>(); try { return objectMapper.readValue(value, List.class); } catch (Exception e) { return new ArrayList<>(); } }
  private Object readJson(String value) { if (value == null || value.isBlank()) return Map.of(); try { return objectMapper.readValue(value, Object.class); } catch (Exception e) { return Map.of(); } }
  private String json(Object value) { try { return value == null ? null : objectMapper.writeValueAsString(value); } catch (JsonProcessingException e) { throw new IllegalStateException("Unable to serialize subscription data", e); } }
}
