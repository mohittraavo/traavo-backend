package com.travo.tcms.platform.service;
import com.travo.tcms.platform.entity.LicenseEntity;
import com.travo.tcms.platform.dto.LicensePayload;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.security.PrivateKey;
import java.time.format.DateTimeFormatter;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import com.travo.tcms.platform.entity.LicenseEntity;

@Service
public class LicenseEmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final LicenseFileService licenseFileService;
    private final PrivateKey signingKey;

    public LicenseEmailService(
            JavaMailSender mailSender,
            SpringTemplateEngine templateEngine,
            LicenseFileService licenseFileService,
            PrivateKey signingKey) {

        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.licenseFileService = licenseFileService;
        this.signingKey = signingKey;
    }

    public void sendLicense(LicenseEntity license) {

        try {
            LicensePayload payload =
                    new LicensePayload(
                            1,
                            license.code,
                            license.schoolName,
                            license.licenseType,
                            license.board,
                            license.academicYear,
                            license.campusCount,
                            license.studentCapacity,
                            license.contactName,
                            license.contactEmail,
                            license.validFrom,
                            license.validTo
                    );

            byte[] licenseFile =
                    licenseFileService.generateLicenseFile(
                            payload,
                            signingKey
                    );

            Context context = new Context();

            context.setVariable(
                    "contactName",
                    license.contactName
            );

            context.setVariable(
                    "licenseCode",
                    license.code
            );

            context.setVariable(
                    "schoolName",
                    license.schoolName
            );

            context.setVariable(
                    "licenseType",
                    license.licenseType
            );

            context.setVariable(
                    "board",
                    license.board
            );

            context.setVariable(
                    "academicYear",
                    license.academicYear
            );

            context.setVariable(
                    "campusCount",
                    license.campusCount
            );

            context.setVariable(
                    "studentCapacity",
                    license.studentCapacity
            );

            context.setVariable(
                    "validFrom",
                    license.validFrom
                            .format(
                                    DateTimeFormatter.ofPattern(
                                            "dd MMM yyyy"
                                    )
                            )
            );

            context.setVariable(
                    "validTo",
                    license.validTo
                            .format(
                                    DateTimeFormatter.ofPattern(
                                            "dd MMM yyyy"
                                    )
                            )
            );

            String html =
                    templateEngine.process(
                            "license-email/index",
                            context
                    );

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true,
                            "UTF-8"
                    );

            helper.setTo(
                    license.contactEmail
            );

            helper.setSubject(
                    "Your Traavo License is Ready - "
                            + license.code
            );

            helper.setText(
                    html,
                    true
            );

            helper.addAttachment(
                    "traavo-" + license.code + ".lic",
                    new ByteArrayResource(
                            licenseFile
                    )
            );

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new IllegalStateException(
                    "Unable to send license email",
                    e
            );
        }
    }
}