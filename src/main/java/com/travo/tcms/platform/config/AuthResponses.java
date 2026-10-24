package com.travo.tcms.platform.config;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

final class AuthResponses {
  private AuthResponses() {
  }

  static void write(HttpServletResponse response, int status, String message) throws IOException {
    response.setStatus(status);
    response.setContentType("application/json");
    String safe = message == null ? "Request failed" : message.replace("\\", "\\\\").replace("\"", "'").replace("\r", " ").replace("\n", " ");
    response.getWriter().write("{\"message\":\"" + safe + "\"}");
  }
}
