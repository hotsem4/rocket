package com.rocket.commons.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

  private final SecretKey secretKey;
  private final long tokenValidTime = 1000L * 60 * 60; // 1시간


  public JwtProvider(@Value("${jwt.secret}") String secret) {
    this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
  }

  // 토큰 생성
  public String generateToken(String email) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + tokenValidTime);

    return Jwts.builder()
        .claim("sub", email)
        .issuedAt(now)
        .expiration(expiry)
        .signWith(secretKey)
        .compact();

  }

  /**
   * 토큰에서 이메일(subject) 추출
   */
  public String getEmail(String token) {
    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .get("sub", String.class);
  }

  /**
   * 토큰 유효성 검사
   */
  public boolean validateToken(String token) {
    try {
      Jwts.parser()
          .verifyWith(secretKey)
          .build()
          .parseSignedClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  /**
   * HTTP 요청 헤더에서 Bearer 토큰 추출
   */
  public String resolveToken(HttpServletRequest request) {
    String bearer = request.getHeader("Authorization");
    if (bearer != null && bearer.startsWith("Bearer ")) {
      return bearer.substring(7);
    }
    return null;
  }
}
