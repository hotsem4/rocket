package com.rocket.commons.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtProviderUnitTest {

  private JwtProvider jwtProvider;
  private final String base64Secret = "dGVzdC1zZWNyZXQta2V5LXRlc3Qtc2VjcmV0LXRlc3Qtc2VjcmV0LXRlc3Qtc2VjcmV0LXRlc3Q=";
  private final String email = "test@rocket.com";

  @BeforeEach
  void setUp() {
    jwtProvider = new JwtProvider(base64Secret);
  }

  @Test
  @DisplayName("AccessToken 생성 및 유효성 검증")
  void createAndValidateAccessToken() {
    String token = jwtProvider.createAccessToken(email);

    assertThat(jwtProvider.validateToken(token)).isTrue();
    assertThat(jwtProvider.isAccessTokenValid(token)).isTrue();
    assertThat(jwtProvider.isRefreshTokenValid(token)).isFalse();
    assertThat(jwtProvider.getEmail(token)).isEqualTo(email);
  }

  @Test
  @DisplayName("RefreshToken 생성 및 유효성 검증")
  void createAndValidateRefreshToken() {
    String token = jwtProvider.createRefreshToken(email);

    assertThat(jwtProvider.validateToken(token)).isTrue();
    assertThat(jwtProvider.isAccessTokenValid(token)).isFalse();
    assertThat(jwtProvider.isRefreshTokenValid(token)).isTrue();
    assertThat(jwtProvider.getEmail(token)).isEqualTo(email);
  }

  @Test
  @DisplayName("만료된 토큰 처리")
  void expiredToken() throws InterruptedException {
    String token = jwtProvider.createToken(email, 1000L, "access");
    Thread.sleep(1100);
    assertThat(jwtProvider.isExpired(token)).isTrue();
    assertThat(jwtProvider.validateToken(token)).isFalse();
  }

  @Test
  @DisplayName("잘못된 형식의 토큰 처리")
  void invalidToken() {
    String invalidToken = "abc.def.ghi";
    assertThat(jwtProvider.validateToken(invalidToken)).isFalse();
    assertThat(jwtProvider.isAccessTokenValid(invalidToken)).isFalse();
    assertThat(jwtProvider.isRefreshTokenValid(invalidToken)).isFalse();
    assertThat(jwtProvider.isExpired(invalidToken)).isFalse();
  }

  @Test
  @DisplayName("tokenType 클레임이 없을 경우 false 반환")
  void tokenWithoutTokenType() {
    String token = Jwts.builder()
        .claim("sub", email)
        .signWith(Keys.hmacShaKeyFor("fakefakefakefakefakefakefakefake".getBytes()))
        .compact();

    assertThat(jwtProvider.isAccessTokenValid(token)).isFalse();
    assertThat(jwtProvider.isRefreshTokenValid(token)).isFalse();
  }

  @Test
  @DisplayName("sub 클레임이 없을 경우 null 반환")
  void getEmailWithoutSub() {
    String token = Jwts.builder()
        .claim("tokenType", "access")
        .signWith(Keys.hmacShaKeyFor("fakefakefakefakefakefakefakefake".getBytes()))
        .compact();

    assertThat(jwtProvider.getEmail(token)).isNull();
  }

  @Test
  @DisplayName("빈 토큰 및 null 토큰은 모두 invalid")
  void nullOrEmptyToken() {
    assertThat(jwtProvider.validateToken(null)).isFalse();
    assertThat(jwtProvider.validateToken("")).isFalse();
    assertThat(jwtProvider.isAccessTokenValid(null)).isFalse();
    assertThat(jwtProvider.isRefreshTokenValid("")).isFalse();
    assertThat(jwtProvider.isExpired(null)).isFalse();
  }
}
