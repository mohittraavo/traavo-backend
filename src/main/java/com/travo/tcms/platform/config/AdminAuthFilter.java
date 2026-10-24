package com.travo.tcms.platform.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;
import com.travo.tcms.platform.service.AuthService;

@Component
public class AdminAuthFilter extends OncePerRequestFilter {

  private final AuthService service;

  public AdminAuthFilter(AuthService service) {
    this.service = service;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    boolean platformArea = path.startsWith("/admin") || path.startsWith("/maintenance");
    return HttpMethod.OPTIONS.matches(request.getMethod()) || path.startsWith("/auth") || !platformArea;
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