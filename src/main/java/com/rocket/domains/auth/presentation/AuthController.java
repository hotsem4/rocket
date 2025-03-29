package com.rocket.domains.auth.presentation;

import com.rocket.commons.security.JwtProvider;
import com.rocket.commons.security.JwtResolver;
import com.rocket.domains.auth.application.dto.response.TokenResponse;
import com.rocket.domains.auth.domain.service.AuthService;
import com.rocket.domains.user.application.dto.request.LoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth Controller", description = "인증 관련 API")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final JwtResolver jwtResolver;
  private final JwtProvider jwtProvider;


  @PostMapping("/login")
  @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인을 시도합니다.")
  public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
    TokenResponse tokenInfo = authService.login(request.email(), request.password());
    return ResponseEntity.ok(tokenInfo);
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(
      @RequestHeader("Authorization") String accessTokenIncludeBearer) {
    String accessToken = jwtResolver.resolveRefreshToken(accessTokenIncludeBearer);
    authService.logout(accessToken);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/access-reissue")
  public ResponseEntity<TokenResponse> accessReissue(
      @RequestHeader("X-Refresh-Token") String refreshTokenIncludeBearer) {
    String refreshToken = jwtResolver.resolveRefreshToken(refreshTokenIncludeBearer);
    return ResponseEntity.ok(authService.accessReissue(refreshToken));
  }

  // RefreshToken 포함 재발급 (RefreshToken 재발급 조건 충족 시 갱신됨)
  @PostMapping("/refresh-reissue")
  public ResponseEntity<TokenResponse> refreshReissue(
      @RequestHeader("Authorization") String accessTokenIncludeBearer) {

    String accessToken = jwtResolver.resolveAccessToken(accessTokenIncludeBearer);
    String email = jwtProvider.getEmail(accessToken);

    TokenResponse tokenResponse = authService.refreshReissue(email, true);
    return ResponseEntity.ok(tokenResponse);
  }

}
