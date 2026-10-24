package com.travo.tcms.platform.config;

import com.travo.tcms.platform.service.SchoolAuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

@Component
public class SchoolAuthFilter extends OncePerRequestFilter {
  private static final List<String> SCHOOL_PREFIXES = List.of(
      "/school/",
      "/api/students",
      "/api/fee-structures",
      "/api/fee-invoices",
      "/api/academics",
      "/api/staff",
      "/api/admissions",
      "/api/teaching-assignments"
  );

  private final SchoolAuthService service;

  public SchoolAuthFilter(SchoolAuthService service) {
    this.service = service;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    if (HttpMethod.OPTIONS.matches(request.getMethod())) {
      return true;
    }
    String path = request.getRequestURI();
    if (path.startsWith("/school/auth/login")) {
      return true;
    }
    return SCHOOL_PREFIXES.stream().noneMatch(path::startsWith);
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      service.requireUserByToken(extractBearerToken(request.getHeader("Authorization")));
      filterChain.doFilter(request, response);
    } catch (ResponseStatusException exception) {
      AuthResponses.write(response, exception.getStatusCode().value(), exception.getReason());
    } catch (io.jsonwebtoken.JwtException | IllegalArgumentException ex) {
      AuthResponses.write(response, HttpStatus.UNAUTHORIZED.value(), "Invalid or expired token");
    } catch (Exception ex) {
      AuthResponses.write(response, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Authentication failed");
    }
  }

  private String extractBearerToken(String authorizationHeader) {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing bearer token");
    }
    return authorizationHeader.substring("Bearer ".length()).trim();
  }
}
