package com.rocket.domains.auth.presentation;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocket.commons.exception.exceptions.InvalidTokenException;
import com.rocket.commons.exception.exceptions.LoginFailedException;
import com.rocket.commons.security.jwt.JwtProvider;
import com.rocket.commons.security.jwt.JwtResolver;
import com.rocket.domains.auth.application.dto.response.TokenResponse;
import com.rocket.domains.auth.domain.service.AuthService;
import com.rocket.domains.user.application.dto.request.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(AuthController.class)
@Import(AuthControllerTest.MockConfig.class)
class AuthControllerTest {

  @TestConfiguration
  static class SecurityTestConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      return http
          .csrf(AbstractHttpConfigurer::disable) // <-- CSRF 비활성화
          .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
          .build();
    }
  }

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private AuthService authService;
  @Autowired
  private JwtProvider jwtProvider;
  @Autowired
  private JwtResolver jwtResolver;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @TestConfiguration
  static class MockConfig {

    @Bean
    public AuthService authService() {
      return mock(AuthService.class);
    }

    @Bean
    public JwtProvider jwtProvider() {
      return mock(JwtProvider.class);
    }

    @Bean
    public JwtResolver jwtResolver() {
      return mock(JwtResolver.class);
    }
  }

  @BeforeEach
  void resetMocks() {
    reset(authService, jwtProvider, jwtResolver);
  }

  @Test
  @DisplayName("로그인 성공 시 200 OK와 토큰 정보 반환")
  void loginSuccess() throws Exception {
    LoginRequest loginRequest = new LoginRequest("test@rocket.com", "password123");
    TokenResponse tokenResponse = new TokenResponse("access-token", "refresh-token",
        "로그인에 성공하였습니다");

    when(authService.login(any(), any())).thenReturn(tokenResponse);

    mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("access-token"))
        .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
        .andExpect(jsonPath("$.message").value("로그인에 성공하였습니다"));
  }

  @Test
  @DisplayName("로그인 실패 시 401 Unauthorized 응답")
  void loginFail() throws Exception {
    LoginRequest loginRequest = new LoginRequest("test@rocket.com", "wrongpassword");

    when(authService.login(any(), any())).thenThrow(new LoginFailedException("로그인 실패"));

    mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("인증이 실패하였습니다."));
  }

  @Test
  @DisplayName("로그아웃 성공 시 204 No Content")
  void logoutSuccess() throws Exception {
    when(jwtResolver.resolveRefreshToken(anyString())).thenReturn("access-token");

    mockMvc.perform(MockMvcRequestBuilders.post("/auth/logout")
            .header("Authorization", "Bearer access-token"))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("로그아웃 실패 - 유효하지 않은 토큰이면 401 응답")
  void logoutFail() throws Exception {
    when(jwtResolver.resolveRefreshToken(anyString())).thenReturn("invalid-token");
    doThrow(new InvalidTokenException("유효하지 않은 토큰입니다.")).when(authService).logout("invalid-token");

    mockMvc.perform(MockMvcRequestBuilders.post("/auth/logout")
            .header("Authorization", "Bearer invalid-token"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("유효하지 않은 토큰입니다."));
  }

  @Test
  @DisplayName("AccessToken 재발급 성공")
  void accessReissueSuccess() throws Exception {
    when(jwtResolver.resolveRefreshToken(anyString())).thenReturn("refresh-token");
    when(authService.accessReissue("refresh-token"))
        .thenReturn(new TokenResponse("new-access", "same-refresh", "AccessToken이 재발급되었습니다."));

    mockMvc.perform(MockMvcRequestBuilders.post("/auth/access-reissue")
            .header("X-Refresh-Token", "Bearer refresh-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("new-access"));
  }

  @Test
  @DisplayName("AccessToken 재발급 실패 - 잘못된 RefreshToken")
  void accessReissueFail() throws Exception {
    when(jwtResolver.resolveRefreshToken(anyString())).thenReturn("bad-refresh-token");
    when(authService.accessReissue("bad-refresh-token"))
        .thenThrow(new InvalidTokenException("유효하지 않은 RefreshToken입니다."));

    mockMvc.perform(MockMvcRequestBuilders.post("/auth/access-reissue")
            .header("X-Refresh-Token", "Bearer bad-refresh-token"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("유효하지 않은 토큰입니다."));
  }

  @Test
  @DisplayName("RefreshToken 재발급 포함 - 성공")
  void refreshReissueSuccess() throws Exception {
    when(jwtResolver.resolveAccessToken(anyString())).thenReturn("access-token");
    when(jwtProvider.getEmail("access-token")).thenReturn("test@rocket.com");
    when(authService.refreshReissue("test@rocket.com", true))
        .thenReturn(new TokenResponse("new-access", "new-refresh", "AccessToken이 재발급되었습니다."));

    mockMvc.perform(MockMvcRequestBuilders.post("/auth/refresh-reissue")
            .header("Authorization", "Bearer access-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("new-access"))
        .andExpect(jsonPath("$.refreshToken").value("new-refresh"));
  }

  @Test
  @DisplayName("로그인 시 이메일이 누락되면 400 Bad Request 반환")
  void loginMissingEmail() throws Exception {
    LoginRequest invalidRequest = new LoginRequest("", "password123");

    mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("로그아웃 시 Authorization 헤더가 없으면 400 또는 401 반환")
  void logoutMissingAuthorizationHeader() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.post("/auth/logout"))
        .andExpect(status().is4xxClientError()); // 400 or 401 depending on handler
  }

  @Test
  @DisplayName("RefreshToken 재발급 시 Authorization 헤더가 없으면 400 또는 401 반환")
  void refreshReissueMissingHeader() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.post("/auth/refresh-reissue"))
        .andExpect(status().is4xxClientError());
  }

  @Test
  @DisplayName("AccessToken을 RefreshToken 자리에 넣으면 401 Unauthorized")
  void accessReissueWithAccessTokenInsteadOfRefreshToken() throws Exception {
    when(jwtResolver.resolveRefreshToken(anyString())).thenReturn("access-token");
    when(authService.accessReissue("access-token"))
        .thenThrow(new InvalidTokenException("RefreshToken 타입이 아닙니다."));

    mockMvc.perform(MockMvcRequestBuilders.post("/auth/access-reissue")
            .header("X-Refresh-Token", "Bearer access-token"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("유효하지 않은 토큰입니다."));
  }

  @Test
  @DisplayName("Authorization 헤더에 'Bearer ' 접두사가 없으면 InvalidTokenException 발생")
  void logoutMissingBearerPrefix() throws Exception {
    String token = "access-token"; // Bearer 접두사 없음

    when(jwtResolver.resolveRefreshToken(token))
        .thenThrow(new InvalidTokenException("유효하지 않은 토큰입니다."));

    mockMvc.perform(MockMvcRequestBuilders.post("/auth/logout")
            .header("Authorization", token)) // Bearer 빠짐
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("유효하지 않은 토큰입니다."));
  }

  @Test
  @DisplayName("Authorization 헤더에 잘못된 토큰 구조 전달 시 401")
  void logoutWithMalformedToken() throws Exception {
    String malformedToken = "not.a.jwt.token";

    when(jwtResolver.resolveRefreshToken(malformedToken))
        .thenThrow(new InvalidTokenException("유효하지 않은 토큰입니다."));

    mockMvc.perform(MockMvcRequestBuilders.post("/auth/logout")
            .header("Authorization", malformedToken))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("유효하지 않은 토큰입니다."));
  }

  @Test
  @DisplayName("RefreshToken 재발급 - TTL 충분한 경우에도 정상 처리")
  void refreshReissueWithValidTokenAndSufficientTTL() throws Exception {
    String accessToken = "Bearer access-token";

    when(jwtResolver.resolveAccessToken("Bearer access-token")).thenReturn("access-token");
    when(jwtProvider.getEmail("access-token")).thenReturn("test@rocket.com");

    TokenResponse response = new TokenResponse("access", "refresh", "갱신 성공");
    when(authService.refreshReissue("test@rocket.com", true)).thenReturn(response);

    mockMvc.perform(MockMvcRequestBuilders.post("/auth/refresh-reissue")
            .header("Authorization", accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("access"))
        .andExpect(jsonPath("$.refreshToken").value("refresh"));
  }

  @Test
  @DisplayName("로그인 요청에 request body가 없으면 400 Bad Request")
  void loginWithNoRequestBody() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("요청 값이 올바르지 않습니다."));
  }

  @Test
  @DisplayName("Content-Type 누락된 로그인 요청은 415 Unsupported Media Type")
  void loginMissingContentType() throws Exception {
    LoginRequest loginRequest = new LoginRequest("test@rocket.com", "password123");

    mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
            .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isUnsupportedMediaType()); // 또는 BadRequest depending on config
  }

}
