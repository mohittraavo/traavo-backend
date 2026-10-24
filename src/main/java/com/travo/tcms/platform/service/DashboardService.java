package com.travo.tcms.platform.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.travo.tcms.platform.repository.*;
import com.travo.tcms.platform.dto.DashboardSummary;
import com.travo.tcms.platform.dto.SubscriptionDashboardItem;
import com.travo.tcms.platform.entity.RoleEntity;
import com.travo.tcms.platform.entity.SubscriptionEntity;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class DashboardService {

  private final SchoolRepository schoolRepository;
  private final TcmsUserRepository userRepository;
  private final LicenseRepository licenseRepository;
  private final TicketRepository ticketRepository;
  private final RoleRepository roleRepository;
  private final com.travo.tcms.platform.repository.SubscriptionRepository subscriptionRepository;

  public DashboardService(SchoolRepository schoolRepository, TcmsUserRepository userRepository,
      LicenseRepository licenseRepository, TicketRepository ticketRepository, RoleRepository roleRepository,
      com.travo.tcms.platform.repository.SubscriptionRepository subscriptionRepository) {
    this.schoolRepository = schoolRepository;
    this.userRepository = userRepository;
    this.licenseRepository = licenseRepository;
    this.ticketRepository = ticketRepository;
    this.roleRepository = roleRepository;
    this.subscriptionRepository = subscriptionRepository;
  }

  public DashboardSummary getDashboardSummary() {
    List<RoleEntity> roles = roleRepository.findAll();
    LocalDate today = LocalDate.now();
    long activeSubscriptions = 0;
    long expiringSubscriptions = 0;
    long graceSubscriptions = 0;
    List<SubscriptionDashboardItem> alerts = new ArrayList<>();
    for (SubscriptionEntity subscription : subscriptionRepository.findAllByOrderByCreatedAtDesc()) {
      if ("ACTIVE".equalsIgnoreCase(subscription.status)) {
        activeSubscriptions++;
      }
      if (subscription.endDate != null && !subscription.endDate.isBefore(today) && ChronoUnit.DAYS.between(today, subscription.endDate) <= 30) {
        expiringSubscriptions++;
      }
      if (subscription.gracePeriodEndDate != null && subscription.endDate != null && today.isAfter(subscription.endDate) && !today.isAfter(subscription.gracePeriodEndDate)) {
        graceSubscriptions++;
      }
      SubscriptionDashboardItem item = dashboardItem(subscription, today);
      if (alerts.size() < 10 && (item.daysRemaining() <= 30 || "GRACE".equals(item.status()) || "SUSPENDED".equals(item.status()))) {
        alerts.add(item);
      }
    }
    return new DashboardSummary(
        schoolRepository.count(),
        userRepository.countByUserType("INTERNAL"),
        licenseRepository.count(),
        licenseRepository.countByStatus("ACTIVATED"),
        ticketRepository.countByStatusIn(List.of("OPEN", "IN_PROGRESS")),
        roles.stream().map(role -> role.roleName).limit(5).toList(),
        activeSubscriptions, expiringSubscriptions, graceSubscriptions, alerts);
  }

        private SubscriptionDashboardItem dashboardItem(SubscriptionEntity subscription, LocalDate today) {
          long daysRemaining = subscription.endDate == null ? Long.MAX_VALUE : ChronoUnit.DAYS.between(today, subscription.endDate);
          String status = subscription.status;
          if (subscription.gracePeriodEndDate != null && subscription.endDate != null && today.isAfter(subscription.endDate) && !today.isAfter(subscription.gracePeriodEndDate)) status = "GRACE";
          return new SubscriptionDashboardItem(subscription.externalId, subscription.schoolExternalId, subscription.schoolName,
          subscription.planName, status, subscription.endDate, subscription.gracePeriodDays, subscription.gracePeriodEndDate, daysRemaining);
        }
}
