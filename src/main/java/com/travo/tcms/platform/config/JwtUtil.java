package com.travo.tcms.platform.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  private final Key signingKey;
  private final long expirationMs;

  public JwtUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration-ms}") long expirationMs) {
    // Use secret bytes as HMAC key; if default not changed, it's fine for local dev but replace in prod
    this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
    this.expirationMs = expirationMs;
  }

  public String generateToken(String sessionId, Long userId, Long tenantId, String role, List<String> permissions) {
    return generateToken(sessionId, userId, tenantId, role, permissions, "tcms");
  }

  public String generateToken(String sessionId, Long userId, Long tenantId, String role, List<String> permissions, String audience) {
    Date now = new Date();
    Date exp = new Date(now.getTime() + expirationMs);
    return Jwts.builder()
        .setId(sessionId)
        .setSubject(userId.toString())
        .setAudience(audience)
        .setIssuedAt(now)
        .setExpiration(exp)
        .claim("tenantId", tenantId)
        .claim("role", role)
        .claim("perms", permissions)
        .signWith(signingKey, SignatureAlgorithm.HS256)
        .compact();
  }

  public Claims validateAndParse(String token) {
    return Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token).getBody();
  }

  @SuppressWarnings("unchecked")
  public static List<String> extractPermissions(Claims claims) {
    Object perms = claims.get("perms");
    if (perms instanceof List) {
      return (List<String>) perms;
    }
    return List.of();
  }

  public static String extractSessionId(Claims claims) {
    return claims.getId();
  }
}