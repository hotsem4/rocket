package com.rocket.commons.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class JwtProvider {

  private final SecretKey secretKey;
  private final long accessTokenValidTime = 1000L * 60 * 30;
  private final long refreshTokenValidTime = 1000L * 60 * 60 * 24 * 7;

  public JwtProvider(@Value("${jwt.secret}") String secret) {
    this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
  }

  public String createAccessToken(String email) {
    return createToken(email, accessTokenValidTime, "access");
  }

  public String createRefreshToken(String email) {
    return createToken(email, refreshTokenValidTime, "refresh");
  }

  // Access 토큰 생성
  public String createToken(String email, long validTime, String tokenType) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + validTime); // 30분짜리 AccessToken

    return Jwts.builder()
        .claim("sub", email)
        .claim("tokenType", tokenType)
        .issuedAt(now)
        .expiration(expiry)
        .signWith(secretKey)
        .compact();
  }

  public Boolean isRefreshTokenValid(String token) {
    try {
      String type = Jwts.parser()
          .verifyWith(secretKey)
          .build()
          .parseSignedClaims(token)
          .getPayload()
          .get("tokenType", String.class);
      return type.equals("refresh");
    } catch (JwtException e) {
      return false;
    }
  }

  public Boolean isAccessTokenValid(String token) {
    try {
      String type = Jwts.parser()
          .verifyWith(secretKey)
          .build()
          .parseSignedClaims(token)
          .getPayload()
          .get("tokenType", String.class);
      return type.equals("access");
    } catch (JwtException e) {
      return false;
    }
  }

  public boolean isExpired(String token) {
    try {
      Jwts.parser()
          .verifyWith(secretKey)
          .build()
          .parseSignedClaims(token);
      return false;
    } catch (ExpiredJwtException e) {
      return true;
    } catch (JwtException e) {
      return false;
    }
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


}
