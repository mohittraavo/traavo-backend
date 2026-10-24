package com.travo.tcms.platform.controller;

import com.travo.tcms.platform.dto.LoginRequest;
import com.travo.tcms.platform.dto.SchoolAuthResponse;
import com.travo.tcms.platform.service.SchoolAuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/school/auth")
public class SchoolAuthController {
  private final SchoolAuthService service;

  public SchoolAuthController(SchoolAuthService service) {
    this.service = service;
  }

  @PostMapping("/login")
  public SchoolAuthResponse login(@Valid @RequestBody LoginRequest request) {
    return service.login(request);
  }

  @GetMapping("/session")
  public SchoolAuthResponse session(@RequestHeader(name = "Authorization", required = false) String authorizationHeader) {
    return service.getSession(extractBearerToken(authorizationHeader));
  }

  @PostMapping("/logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void logout(@RequestHeader(name = "Authorization", required = false) String authorizationHeader) {
    service.logout(extractBearerToken(authorizationHeader));
  }

  private String extractBearerToken(String authorizationHeader) {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing bearer token");
    }
    return authorizationHeader.substring("Bearer ".length()).trim();
  }
}
