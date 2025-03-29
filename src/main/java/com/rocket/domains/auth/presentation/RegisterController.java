package com.rocket.domains.auth.presentation;

import com.rocket.domains.auth.application.dto.response.TokenResponse;
import com.rocket.domains.auth.domain.service.AuthService;
import com.rocket.domains.user.application.dto.request.UserRegisterRequest;
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
@RequestMapping("/register")
@Tag(name = "Register Controller", description = "회원가입 관련 API")
@RequiredArgsConstructor
public class RegisterController {

  private final AuthService authService;

  @PostMapping
  @Operation(summary = "회원 가입", description = "새로운 사용자를 등록합니다.")
  public ResponseEntity<TokenResponse> register(@Valid @RequestBody UserRegisterRequest request) {
    return ResponseEntity.ok(authService.register(request));
  }
}
