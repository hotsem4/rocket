package com.rocket.commons.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtResolverUnitTest {

  private final JwtResolver jwtResolver = new JwtResolver();

  @Test
  @DisplayName("AccessToken에서 'Bearer ' 접두어를 제거하고 토큰만 반환한다.")
  void resolveAccessToken_withBearerPrefix_returnsToken() {
    String bearerToken = "Bearer abc.def.ghi";
    String result = jwtResolver.resolveAccessToken(bearerToken);

    assertThat(result).isEqualTo("abc.def.ghi");
  }

  @Test
  @DisplayName("RefreshToken에서 'Bearer ' 접두어를 제거하고 토큰만 반환한다.")
  void resolveRefreshToken_withBearerPrefix_returnsToken() {
    String bearerToken = "Bearer xyz.123.456";
    String result = jwtResolver.resolveRefreshToken(bearerToken);

    assertThat(result).isEqualTo("xyz.123.456");
  }

  @Test
  @DisplayName("접두어가 없거나 null이면 null을 반환한다.")
  void resolveToken_whenMissingBearer_returnsNull() {
    assertThat(jwtResolver.resolveAccessToken(null)).isNull();
    assertThat(jwtResolver.resolveAccessToken("abc.def.ghi")).isNull();
    assertThat(jwtResolver.resolveRefreshToken("")).isNull();
  }


}
