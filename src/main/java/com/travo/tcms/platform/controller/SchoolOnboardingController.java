package com.travo.tcms.platform.controller;

import com.travo.tcms.platform.dto.SchoolOnboardingResponse;
import com.travo.tcms.platform.dto.SchoolOnboardingRequest;
import com.travo.tcms.platform.service.SchoolOnboardingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/schools")
public class SchoolOnboardingController {

    private final SchoolOnboardingService schoolOnboardingService;

    public SchoolOnboardingController(SchoolOnboardingService schoolOnboardingService) {
        this.schoolOnboardingService = schoolOnboardingService;
    }

    @PostMapping
    public ResponseEntity<SchoolOnboardingResponse> createSchool(@Valid @RequestBody SchoolOnboardingRequest request) {
        SchoolOnboardingResponse response = schoolOnboardingService.createSchool(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
