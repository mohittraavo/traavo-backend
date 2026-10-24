package com.travo.tcms.platform.controller;

import com.travo.tcms.platform.service.SubscriptionManagementService;
import com.travo.tcms.platform.service.SubscriptionPlanService;
import java.util.List;
import java.util.Map;
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

@RestController
@RequestMapping("/admin/subscription-management")
public class SubscriptionManagementController {
  private final SubscriptionPlanService planService;
  private final SubscriptionManagementService subscriptionService;

  public SubscriptionManagementController(SubscriptionPlanService planService, SubscriptionManagementService subscriptionService) {
    this.planService = planService;
    this.subscriptionService = subscriptionService;
  }

  @GetMapping("/plans")
  public List<Map<String, Object>> listPlans() { return planService.listPlans(); }

  @PostMapping("/plans")
  @ResponseStatus(HttpStatus.CREATED)
  public Map<String, Object> createPlan(@RequestBody Map<String, Object> body) { return planService.createPlan(body); }

  @PutMapping("/plans/{id}")
  public Map<String, Object> updatePlan(@PathVariable String id, @RequestBody Map<String, Object> body) { return planService.updatePlan(id, body); }

  @PostMapping("/plans/{id}/clone")
  public Map<String, Object> clonePlan(@PathVariable String id) { return planService.clonePlan(id); }

  @PutMapping("/plans/{id}/status")
  public Map<String, Object> changePlanStatus(@PathVariable String id, @RequestBody Map<String, Object> body) { return planService.changeStatus(id, String.valueOf(body.getOrDefault("status", ""))); }

  @GetMapping("/subscriptions")
  public List<Map<String, Object>> listSubscriptions() { return subscriptionService.listSubscriptions(); }

  @PostMapping("/schools/{schoolId}/subscriptions")
  @ResponseStatus(HttpStatus.CREATED)
  public Map<String, Object> assign(@PathVariable String schoolId, @RequestBody Map<String, Object> body) { return subscriptionService.assign(schoolId, body); }

  @PutMapping("/subscriptions/{id}/suspend")
  public Map<String, Object> suspend(@PathVariable String id) { return subscriptionService.changeStatus(id, "SUSPENDED", "SUBSCRIPTION_SUSPENDED"); }

  @PutMapping("/subscriptions/{id}/reactivate")
  public Map<String, Object> reactivate(@PathVariable String id) { return subscriptionService.changeStatus(id, "ACTIVE", "SUBSCRIPTION_REACTIVATED"); }

  @PutMapping("/subscriptions/{id}/cancel")
  public Map<String, Object> cancel(@PathVariable String id) { return subscriptionService.changeStatus(id, "CANCELLED", "SUBSCRIPTION_CANCELLED"); }

  @PutMapping("/subscriptions/{id}/upgrade")
  public Map<String, Object> upgrade(@PathVariable String id, @RequestBody Map<String, Object> body) { return subscriptionService.upgrade(id, String.valueOf(body.get("planId"))); }

  @PutMapping("/subscriptions/{id}/downgrade")
  public Map<String, Object> downgrade(@PathVariable String id, @RequestBody Map<String, Object> body) { return subscriptionService.downgrade(id, String.valueOf(body.get("planId"))); }

  @GetMapping("/subscriptions/{id}/usage")
  public Map<String, Object> usage(@PathVariable String id) { return subscriptionService.usage(id); }

  @GetMapping("/subscriptions/{id}/access")
  public Map<String, Object> effectiveAccess(@PathVariable String id) { return subscriptionService.effectiveAccess(id); }

  @GetMapping("/subscriptions/{id}/audit")
  public List<Map<String, Object>> audit(@PathVariable String id) { return subscriptionService.audit(id); }
}