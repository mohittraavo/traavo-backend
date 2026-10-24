package com.travo.tcms.platform.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import com.travo.tcms.platform.dto.*;
import com.travo.tcms.platform.service.SchoolService;
import com.travo.tcms.platform.service.SubscriptionPlanService;
import com.travo.tcms.platform.service.SubscriptionManagementService;

@RestController
@RequestMapping("/admin")
public class SchoolController {
  private final SchoolService schoolService;
  private final SubscriptionPlanService subscriptionPlanService;
  private final SubscriptionManagementService subscriptionManagementService;

  public SchoolController(SchoolService schoolService, SubscriptionPlanService subscriptionPlanService,
      SubscriptionManagementService subscriptionManagementService) {
    this.schoolService = schoolService;
    this.subscriptionPlanService = subscriptionPlanService;
    this.subscriptionManagementService = subscriptionManagementService;
  }

  @GetMapping("/schools")
  public List<SchoolListRecord> listSchools() {
    return schoolService.listSchools();
  }

  @GetMapping("/schools/{externalId}")
  public SchoolDetailRecord getSchool(@PathVariable String externalId) {
    return schoolService.getSchool(externalId);
  }

  @GetMapping("/campuses")
  public List<CampusListRecord> listCampuses() {
    return schoolService.listCampuses();
  }

  @GetMapping("/campuses/{externalId}")
  public CampusDetailRecord getCampus(@PathVariable String externalId) {
    return schoolService.getCampus(externalId);
  }

  @GetMapping("/campuses/capacity")
  public List<CampusCapacityRecord> listCampusCapacity() {
    return schoolService.listCampusCapacity();
  }

  // Subscription endpoints
  @GetMapping("/subscription-plans")
  public List<java.util.Map<String, Object>> listSubscriptionPlans() {
    return subscriptionPlanService.listPlans();
  }

  @GetMapping("/schools/{externalId}/subscriptions")
  public List<java.util.Map<String, Object>> listSchoolSubscriptions(
      @PathVariable String externalId) {
    return subscriptionManagementService.listSchoolSubscriptions(externalId);
  }

  // Backwards-compatible alias: support GET /admin/subscriptions
  @GetMapping("/subscriptions")
  public List<java.util.Map<String, Object>> listSubscriptionPlansAlias() {
    return subscriptionManagementService.listSubscriptions();
  }

  // Create a new subscription plan (accepts flexible JSON keys like planCode/planName/basePrice)
  @PostMapping("/subscription-plans")
  @org.springframework.web.bind.annotation.ResponseStatus(org.springframework.http.HttpStatus.CREATED)
  public java.util.Map<String, Object> createSubscriptionPlan(
      @org.springframework.web.bind.annotation.RequestBody java.util.Map<String, Object> body) {

    String rawId = body.containsKey("planCode") ? String.valueOf(body.get("planCode"))
        : body.containsKey("id") ? String.valueOf(body.get("id")) : "";
    String id = (rawId == null || rawId.isBlank()) ? java.util.UUID.randomUUID().toString() : rawId;
    String name = body.containsKey("planName") ? String.valueOf(body.get("planName"))
        : body.containsKey("name") ? String.valueOf(body.get("name")) : "Unnamed";
    String status = body.containsKey("status") ? String.valueOf(body.get("status")) : "DRAFT";

    return subscriptionPlanService.createPlan(body);
  }

  @PostMapping("/schools/{externalId}/subscriptions")
  public java.util.Map<String, Object> createSubscription(@PathVariable String externalId,
      @org.springframework.web.bind.annotation.RequestBody java.util.Map<String, Object> req) {
    return subscriptionManagementService.assign(externalId, req);
  }
}
