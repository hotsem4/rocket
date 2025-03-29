package com.rocket.domains.auth.presentation;

import com.rocket.domains.auth.domain.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
@Tag(name = "Email Check Controller", description = "이메일 중복 확인 관련 API")
@RequiredArgsConstructor
public class EmailCheckController {

  private final AuthService authService;

  @GetMapping("/check")
  @Operation(summary = "이메일 중복 확인", description = "이메일이 중복되었는지 확인한다.")
  public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
    authService.existsByEmail(email);
    return ResponseEntity.noContent().build();
  }
}
