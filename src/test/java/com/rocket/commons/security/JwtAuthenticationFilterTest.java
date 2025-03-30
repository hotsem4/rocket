package com.rocket.commons.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rocket.domains.auth.domain.repository.RefreshTokenStore;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

class JwtAuthenticationFilterTest {

  @InjectMocks
  private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Mock
  private JwtProvider jwtProvider;
  @Mock
  private JwtResolver jwtResolver;
  @Mock
  private AuthenticationManager authenticationManager;
  @Mock
  private RefreshTokenStore refreshTokenStore;
  @Mock
  private FilterChain filterChain;

  private final String email = "test@rocket.com";
  private final String accessToken = "validAccessToken";
  private final String refreshToken = "validRefreshToken";
  private final String newAccessToken = "newAccessToken";

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtProvider, jwtResolver,
        authenticationManager, refreshTokenStore);
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }


  @Test
  @DisplayName("유효한 AccessToken이 있을 경우 인증이 성공해야 한다.")
  void validAccessTokenShouldAuthenticate() throws Exception {
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer " + accessToken);
    MockHttpServletResponse response = new MockHttpServletResponse();

    when(jwtResolver.resolveAccessToken(anyString())).thenReturn(accessToken);
    when(jwtProvider.validateToken(accessToken)).thenReturn(true);
    when(jwtProvider.getEmail(accessToken)).thenReturn(email);

    Authentication auth = mock(Authentication.class);
    when(authenticationManager.authenticate(any())).thenReturn(auth);

    // when
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // then
    verify(authenticationManager).authenticate(any(PreAuthenticatedAuthenticationToken.class));
    verify(filterChain).doFilter(request, response);
  }

  @Test
  @DisplayName("AccessToken이 만료되고 유효한 RefreshToken이 존재하면 새로운 AccessToken이 발급된다.")
  void expiredAccessTokenWithValidRefreshTokenIssuesNewAccessToken() throws Exception {
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer expiredToken");
    request.addHeader("X-Refresh-Token", refreshToken);
    MockHttpServletResponse response = new MockHttpServletResponse();

    when(jwtResolver.resolveAccessToken(anyString())).thenReturn("expiredToken");
    when(jwtProvider.validateToken("expiredToken")).thenReturn(false);
    when(jwtProvider.isExpired("expiredToken")).thenReturn(true);

    when(jwtResolver.resolveRefreshToken(anyString())).thenReturn(refreshToken);
    when(jwtProvider.validateToken(refreshToken)).thenReturn(true);
    when(jwtProvider.isRefreshTokenValid(refreshToken)).thenReturn(true);
    when(jwtProvider.getEmail(refreshToken)).thenReturn(email);
    when(refreshTokenStore.get(email)).thenReturn(refreshToken);
    when(jwtProvider.createAccessToken(email)).thenReturn(newAccessToken);

    Authentication auth = mock(Authentication.class);
    when(authenticationManager.authenticate(any())).thenReturn(auth);

    // when
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // then
    verify(jwtProvider).createAccessToken(email);
    assertThat(response.getHeader("X-New-Access-Token")).isEqualTo(newAccessToken);
    verify(filterChain).doFilter(request, response);
  }

  @Test
  @DisplayName("AccessToken과 RefreshToken이 모두 유효하지 않은 경우 인증이 수행되지 않는다.")
  void invalidTokensShouldNotAuthenticate() throws Exception {
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer invalidToken");
    request.addHeader("X-Refresh-Token", "badRefreshToken");
    MockHttpServletResponse response = new MockHttpServletResponse();

    when(jwtResolver.resolveAccessToken(anyString())).thenReturn("invalidToken");
    when(jwtProvider.validateToken("invalidToken")).thenReturn(false);
    when(jwtProvider.isExpired("invalidToken")).thenReturn(false);

    // when
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // then
    verify(authenticationManager, never()).authenticate(any());
    verify(filterChain).doFilter(request, response);
    assertThat(response.getHeader("X-New-Access-Token")).isNull();
  }

  @DisplayName("Authorization 헤더가 없는 경우 필터가 통과한다")
  @Test
  void noAuthorizationHeader() throws Exception {
    // given
    MockHttpServletRequest request = new MockHttpServletRequest(); // Authorization 헤더 없음
    MockHttpServletResponse response = new MockHttpServletResponse();

    when(jwtResolver.resolveAccessToken(null)).thenReturn(null);

    // when
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // then
    verify(authenticationManager, never()).authenticate(any());
    verify(filterChain).doFilter(request, response);
  }

  @DisplayName("RefreshToken이 Redis 값과 다르면 재발급되지 않는다")
  @Test
  void refreshTokenMismatchWithRedis() throws Exception {
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer expiredToken");
    request.addHeader("X-Refresh-Token", refreshToken);
    MockHttpServletResponse response = new MockHttpServletResponse();

    when(jwtResolver.resolveAccessToken(anyString())).thenReturn("expiredToken");
    when(jwtProvider.validateToken("expiredToken")).thenReturn(false);
    when(jwtProvider.isExpired("expiredToken")).thenReturn(true);

    when(jwtResolver.resolveRefreshToken(anyString())).thenReturn(refreshToken);
    when(jwtProvider.validateToken(refreshToken)).thenReturn(true);
    when(jwtProvider.isRefreshTokenValid(refreshToken)).thenReturn(true);
    when(jwtProvider.getEmail(refreshToken)).thenReturn(email);
    when(refreshTokenStore.get(email)).thenReturn("differentToken");

    // when
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // then
    verify(jwtProvider, never()).createAccessToken(any());
    verify(authenticationManager, never()).authenticate(any());
    assertThat(response.getHeader("X-New-Access-Token")).isNull();
  }

  @DisplayName("AccessToken이 만료됐지만 RefreshToken이 없으면 인증되지 않는다")
  @Test
  void expiredAccessTokenWithoutRefreshToken() throws Exception {
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer expiredToken");
    MockHttpServletResponse response = new MockHttpServletResponse();

    when(jwtResolver.resolveAccessToken(anyString())).thenReturn("expiredToken");
    when(jwtProvider.validateToken("expiredToken")).thenReturn(false);
    when(jwtProvider.isExpired("expiredToken")).thenReturn(true);
    when(jwtResolver.resolveRefreshToken(null)).thenReturn(null);

    // when
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // then
    verify(authenticationManager, never()).authenticate(any());
    assertThat(response.getHeader("X-New-Access-Token")).isNull();
  }

  @DisplayName("RefreshToken이 존재하지만 유효하지 않으면 인증되지 않는다")
  @Test
  void invalidRefreshTokenShouldNotAuthenticate() throws Exception {
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer expiredToken");
    request.addHeader("X-Refresh-Token", refreshToken);
    MockHttpServletResponse response = new MockHttpServletResponse();

    when(jwtResolver.resolveAccessToken(anyString())).thenReturn("expiredToken");
    when(jwtProvider.validateToken("expiredToken")).thenReturn(false);
    when(jwtProvider.isExpired("expiredToken")).thenReturn(true);

    when(jwtResolver.resolveRefreshToken(anyString())).thenReturn(refreshToken);
    when(jwtProvider.validateToken(refreshToken)).thenReturn(false);

    // when
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // then
    verify(authenticationManager, never()).authenticate(any());
    verify(jwtProvider, never()).createAccessToken(any());
    assertThat(response.getHeader("X-New-Access-Token")).isNull();
  }

  @DisplayName("AccessToken이 유효하면 SecurityContextHolder에 인증 정보가 저장된다")
  @Test
  void authenticationIsSetInSecurityContextHolder() throws Exception {
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer " + accessToken);
    MockHttpServletResponse response = new MockHttpServletResponse();

    when(jwtResolver.resolveAccessToken(anyString())).thenReturn(accessToken);
    when(jwtProvider.validateToken(accessToken)).thenReturn(true);
    when(jwtProvider.getEmail(accessToken)).thenReturn(email);

    Authentication mockAuth = mock(Authentication.class);
    when(authenticationManager.authenticate(any())).thenReturn(mockAuth);

    // 👉 테스트 전 기존 인증 정보 초기화
    SecurityContextHolder.clearContext();

    // when
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // then
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    assertThat(authentication).isNotNull();
    assertThat(authentication).isEqualTo(mockAuth);  // mocking된 Authentication 객체와 일치
    verify(filterChain).doFilter(request, response);
  }

  @DisplayName("AccessToken이 유효하지 않으면 SecurityContextHolder에 인증 정보가 저장되지 않는다")
  @Test
  void authenticationIsNotSetWhenTokenInvalid() throws Exception {
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer invalidToken");
    MockHttpServletResponse response = new MockHttpServletResponse();

    when(jwtResolver.resolveAccessToken(anyString())).thenReturn("invalidToken");
    when(jwtProvider.validateToken("invalidToken")).thenReturn(false);
    when(jwtProvider.isExpired("invalidToken")).thenReturn(false); // 단순히 유효하지 않음

    // 👉 테스트 전 인증 정보 초기화
    SecurityContextHolder.clearContext();

    // when
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // then
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertThat(authentication).isNull();

    verify(authenticationManager, never()).authenticate(any());
    verify(filterChain).doFilter(request, response);
  }


}
