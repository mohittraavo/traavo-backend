package com.travo.tcms.platform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travo.tcms.platform.dto.SubscriptionPlanRecord;
import com.travo.tcms.platform.entity.SchoolEntity;
import com.travo.tcms.platform.entity.SubscriptionAuditEntity;
import com.travo.tcms.platform.entity.SubscriptionEntity;
import com.travo.tcms.platform.entity.SubscriptionPlanEntity;
import com.travo.tcms.platform.repository.SchoolRepository;
import com.travo.tcms.platform.repository.SubscriptionAuditRepository;
import com.travo.tcms.platform.repository.SubscriptionRepository;
import java.time.LocalDate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SubscriptionManagementService {
  private final SubscriptionRepository subscriptionRepository;
  private final SubscriptionPlanService planService;
  private final SchoolRepository schoolRepository;
  private final SubscriptionAuditRepository auditRepository;
  private final SubscriptionEmailService emailService;
  private final SchoolAccessService schoolAccessService;
  private final ObjectMapper objectMapper;
  private final String portalUrl;

  public SubscriptionManagementService(SubscriptionRepository subscriptionRepository, SubscriptionPlanService planService,
      SchoolRepository schoolRepository, SubscriptionAuditRepository auditRepository,
      SubscriptionEmailService emailService, SchoolAccessService schoolAccessService, ObjectMapper objectMapper,
      @Value("${school.portal-url:https://school.traavo.in}") String portalUrl) {
    this.subscriptionRepository = subscriptionRepository;
    this.planService = planService;
    this.schoolRepository = schoolRepository;
    this.auditRepository = auditRepository;
    this.emailService = emailService;
    this.schoolAccessService = schoolAccessService;
    this.objectMapper = objectMapper;
    this.portalUrl = portalUrl;
  }

  public List<Map<String, Object>> listSubscriptions() {
    return subscriptionRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toMap).toList();
  }

  public List<Map<String, Object>> listSchoolSubscriptions(String schoolId) {
    return subscriptionRepository.findBySchoolExternalIdOrderByCreatedAtDesc(schoolId).stream().map(this::toMap).toList();
  }

  @Transactional
  public Map<String, Object> assign(String schoolId, Map<String, Object> request) {
    SchoolEntity school = schoolRepository.findByExternalId(schoolId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "School not found"));
    String planId = text(request, "planId", "");
    SubscriptionPlanEntity plan = planService.getPlan(planId);
    if (!"ACTIVE".equalsIgnoreCase(plan.status)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only active plans can be assigned");
    if (subscriptionRepository.findFirstBySchoolExternalIdAndStatusIgnoreCase(schoolId, "ACTIVE").isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "This school already has an active subscription.");
    }
    String billing = text(request, "billingCycle", plan.billingCycle);
    LocalDate start = date(request, "startDate", LocalDate.now());
    LocalDate end = date(request, "endDate", calculateEndDate(start, billing));
    int gracePeriodDays = integer(request, "gracePeriodDays", 0);
    if (gracePeriodDays < 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Grace period cannot be negative");
    SubscriptionEntity entity = new SubscriptionEntity();
    entity.externalId = "SUB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    entity.schoolExternalId = school.externalId; entity.schoolName = school.schoolName;
    entity.planExternalId = plan.externalId; entity.planName = plan.name; entity.status = "ACTIVE";
    entity.billingCycle = billing.toUpperCase(); entity.startDate = start; entity.endDate = end;
    entity.gracePeriodDays = gracePeriodDays; entity.gracePeriodEndDate = end.plusDays(gracePeriodDays);
    entity.price = plan.offerPrice == null ? plan.price : plan.offerPrice; entity.studentLimit = plan.studentLimit; entity.campusLimit = plan.campusLimit;
    entity.modulesJson = plan.modulesJson; entity.featuresJson = plan.featuresJson;
    entity.usageJson = json(Map.of("students", school.totalStudents, "staff", school.teachers, "users", school.activeUsers, "campuses", school.totalCampuses));
    entity.createdAt = Instant.now(); entity.updatedAt = entity.createdAt;
    subscriptionRepository.save(entity);
    school.subscriptionPlan = plan.name; school.subscriptionStartDate = start; school.subscriptionExpiryDate = end;
    school.billingCycle = entity.billingCycle; school.paymentStatus = "ACTIVE"; school.studentLimit = entity.studentLimit; school.campusLimit = entity.campusLimit;
    school.subscriptionGracePeriodDays = gracePeriodDays; school.subscriptionGracePeriodEndDate = entity.gracePeriodEndDate;
    school.featureFlagsCsv = String.join("|", listJson(plan.featuresJson).stream().map(String::valueOf).toList()); schoolRepository.save(school);
    audit(entity, "SUBSCRIPTION_ASSIGNED", null, toMap(entity));
    SchoolAccessService.SchoolAccess access = schoolAccessService.provision(school);
    SubscriptionPlanRecord planRecord = new SubscriptionPlanRecord(plan.externalId, plan.name, plan.price, plan.offerPrice, plan.billingCycle, plan.studentLimit, plan.campusLimit, listJson(plan.featuresJson).stream().map(String::valueOf).toList());
    boolean emailSent = emailService.sendSubscriptionEmail(school, planRecord, access.email(), access.temporaryPassword(), portalUrl);
    Map<String, Object> result = toMap(entity);
    result.put("schoolAccessEmail", access.email());
    result.put("portalUrl", portalUrl);
    result.put("emailSent", emailSent);
    if (access.temporaryPassword() != null) {
      result.put("temporaryPassword", access.temporaryPassword());
    }
    return result;
  }

  public Map<String, Object> changeStatus(String id, String status, String action) {
    SubscriptionEntity entity = get(id);
    String normalized = status.toUpperCase();
    if (normalized.equals("ACTIVE") && !("SUSPENDED".equalsIgnoreCase(entity.status) || "TRIAL".equalsIgnoreCase(entity.status))) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only suspended or trial subscriptions can be reactivated");
    String old = entity.status; entity.status = normalized; entity.updatedAt = Instant.now(); subscriptionRepository.save(entity);
    audit(entity, action, Map.of("status", old), Map.of("status", normalized)); return toMap(entity);
  }

  public Map<String, Object> upgrade(String id, String planId) { return changePlan(id, planId, "SUBSCRIPTION_UPGRADED"); }
  public Map<String, Object> downgrade(String id, String planId) { return changePlan(id, planId, "SUBSCRIPTION_DOWNGRADED"); }

  public Map<String, Object> usage(String id) {
    SubscriptionEntity entity = get(id); Map<String, Object> result = new LinkedHashMap<>();
    result.put("subscriptionId", entity.externalId); result.put("usage", readJson(entity.usageJson)); result.put("studentLimit", entity.studentLimit); result.put("campusLimit", entity.campusLimit); return result;
  }

  public Map<String, Object> effectiveAccess(String id) {
    SubscriptionEntity entity = get(id);
    Map<String, Object> result = new LinkedHashMap<>();
    result.put("subscriptionId", entity.externalId);
    result.put("status", entity.status);
    result.put("modules", "ACTIVE".equalsIgnoreCase(entity.status) || "TRIAL".equalsIgnoreCase(entity.status) ? listJson(entity.modulesJson) : List.of());
    result.put("features", "ACTIVE".equalsIgnoreCase(entity.status) || "TRIAL".equalsIgnoreCase(entity.status) ? listJson(entity.featuresJson) : List.of());
    return result;
  }

  public List<Map<String, Object>> audit(String id) {
    return auditRepository.findByEntityExternalIdOrderByCreatedAtDesc(id).stream().map(a -> Map.of("id", a.id, "action", a.action, "entityType", a.entityType, "oldValue", readJson(a.oldValue), "newValue", readJson(a.newValue), "createdAt", a.createdAt)).toList();
  }

  private Map<String, Object> changePlan(String id, String planId, String action) {
    SubscriptionEntity entity = get(id); SubscriptionPlanEntity plan = planService.getPlan(planId);
    if (!"ACTIVE".equalsIgnoreCase(plan.status)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only active plans can be assigned");
    if (action.endsWith("DOWNGRADED")) {
      Map<?, ?> usage = readJson(entity.usageJson) instanceof Map<?, ?> map ? map : Map.of();
      int studentsUsed = integerValue(usage.get("students"));
      if (plan.studentLimit > 0 && studentsUsed > plan.studentLimit) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Current student usage exceeds the selected plan limit by " + (studentsUsed - plan.studentLimit));
      }
    }
    Map<String, Object> old = toMap(entity); entity.planExternalId = plan.externalId; entity.planName = plan.name; entity.price = plan.offerPrice == null ? plan.price : plan.offerPrice; entity.studentLimit = plan.studentLimit; entity.campusLimit = plan.campusLimit; entity.modulesJson = plan.modulesJson; entity.featuresJson = plan.featuresJson; entity.updatedAt = Instant.now(); subscriptionRepository.save(entity); audit(entity, action, old, toMap(entity)); return toMap(entity);
  }

  private SubscriptionEntity get(String id) { return subscriptionRepository.findByExternalId(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found")); }
  private LocalDate calculateEndDate(LocalDate start, String billing) { String value = billing == null ? "MONTHLY" : billing.toUpperCase(); if (value.equals("YEARLY") || value.equals("ANNUAL")) return start.plusYears(1); if (value.equals("QUARTERLY")) return start.plusMonths(3); return start.plusMonths(1); }
  private String text(Map<String, Object> map, String key, String fallback) { Object value = map == null ? null : map.get(key); return value == null || String.valueOf(value).isBlank() ? fallback : String.valueOf(value); }
  private int integer(Map<String, Object> map, String key, int fallback) { try { return map != null && map.get(key) != null && !String.valueOf(map.get(key)).isBlank() ? Integer.parseInt(String.valueOf(map.get(key))) : fallback; } catch (NumberFormatException e) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, key + " must be an integer"); } }
  private LocalDate date(Map<String, Object> map, String key, LocalDate fallback) { try { return map != null && map.get(key) != null && !String.valueOf(map.get(key)).isBlank() ? LocalDate.parse(String.valueOf(map.get(key))) : fallback; } catch (Exception e) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, key + " must be a valid date"); } }
  private SubscriptionAuditEntity audit(SubscriptionEntity entity, String action, Object oldValue, Object newValue) { SubscriptionAuditEntity audit = new SubscriptionAuditEntity(); audit.entityType = "SUBSCRIPTION"; audit.entityExternalId = entity.externalId; audit.schoolExternalId = entity.schoolExternalId; audit.action = action; audit.performedBy = "CURRENT_USER"; audit.oldValue = json(oldValue); audit.newValue = json(newValue); audit.createdAt = Instant.now(); return auditRepository.save(audit); }
  private Map<String, Object> toMap(SubscriptionEntity e) { Map<String, Object> m = new LinkedHashMap<>(); m.put("id", e.externalId); m.put("subscriptionId", e.externalId); m.put("schoolExternalId", e.schoolExternalId); m.put("schoolName", e.schoolName); m.put("planId", e.planExternalId); m.put("planName", e.planName); m.put("status", e.status); m.put("billingCycle", e.billingCycle); m.put("startDate", e.startDate); m.put("endDate", e.endDate); m.put("gracePeriodDays", e.gracePeriodDays); m.put("gracePeriodEndDate", e.gracePeriodEndDate); m.put("price", e.price); m.put("studentLimit", e.studentLimit); m.put("campusLimit", e.campusLimit); m.put("modules", listJson(e.modulesJson)); m.put("featureFlags", listJson(e.featuresJson)); return m; }
  private List<Object> listJson(String value) { if (value == null || value.isBlank()) return new ArrayList<>(); try { return objectMapper.readValue(value, List.class); } catch (Exception e) { return new ArrayList<>(); } }
  private Object readJson(String value) { try { return value == null ? Map.of() : objectMapper.readValue(value, Object.class); } catch (Exception e) { return Map.of(); } }
  private int integerValue(Object value) { try { return value == null ? 0 : Integer.parseInt(String.valueOf(value)); } catch (NumberFormatException e) { return 0; } }
  private String json(Object value) { try { return value == null ? null : objectMapper.writeValueAsString(value); } catch (JsonProcessingException e) { throw new IllegalStateException(e); } }
}
