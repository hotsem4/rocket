package com.rocket.domains.auth.presentation;

import com.rocket.domains.user.application.dto.request.LoginRequest;
import com.rocket.domains.user.application.dto.request.UserRegisterRequest;
import com.rocket.domains.user.application.dto.response.UserInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth Controller", description = "인증 관련 API")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  @Operation(summary = "회원 가입", description = "새로운 사용자를 등록합니다.")
  public ResponseEntity<String> register(@Valid @RequestBody UserRegisterRequest request) {
    authService.register(request);
    return ResponseEntity.ok("계정 생성에 성공하였습니다.");
  }

  @PostMapping("/login")
  @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인을 시도합니다.")
  public ResponseEntity<UserInfoResponse> login(@Valid @RequestBody LoginRequest request) {
    UserInfoResponse userInfo = authService.login(request.email(), request.password());
    return ResponseEntity.ok(userInfo);
  }
}
