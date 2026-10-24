package com.travo.tcms.platform.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import com.travo.tcms.platform.entity.SchoolEntity;
import com.travo.tcms.platform.dto.SubscriptionPlanRecord;

@Service
public class SubscriptionEmailService {

  private final JavaMailSender mailSender;
  private final SpringTemplateEngine templateEngine;

  public SubscriptionEmailService(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
    this.mailSender = mailSender;
    this.templateEngine = templateEngine;
  }

  public void sendSubscriptionEmail(SchoolEntity school, SubscriptionPlanRecord plan) {
    try {
      Context ctx = new Context();
      ctx.setVariable("schoolName", school.schoolName);
      ctx.setVariable("contactName", school.schoolAdminName != null ? school.schoolAdminName : "Administrator");
      ctx.setVariable("planName", plan.name());
      ctx.setVariable("price", plan.offerPrice() != null ? plan.offerPrice() : plan.price());
      ctx.setVariable("billingCycle", plan.billingCycle());
      ctx.setVariable("features", plan.features());

      String html = templateEngine.process("subscription-email/index", ctx);

      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      final String to = school.schoolAdminEmail != null ? school.schoolAdminEmail : school.email;
      helper.setTo(to);
      helper.setSubject("Subscription Offer from Traavo — " + plan.name());
      helper.setText(html, true);

      String summary = "Plan: " + plan.name() + "\nPrice: " + (plan.offerPrice() != null ? plan.offerPrice() : plan.price()) + "\nBilling: " + plan.billingCycle();
      helper.addAttachment("subscription-summary.txt", new ByteArrayResource(summary.getBytes()));

      mailSender.send(message);
    } catch (MessagingException | RuntimeException e) {
      System.err.println("Info: subscription email skipped for school " + school.externalId + " because mail is not configured: " + e.getMessage());
    }
  }
}
