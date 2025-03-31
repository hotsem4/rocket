package com.rocket.domains.user.presentation;

import com.rocket.domains.user.application.dto.request.PasswordChangeByTokenRequest;
import com.rocket.domains.user.application.service.passwordReset.PasswordChanger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@RequiredArgsConstructor
@Tag(name = "Password Reset", description = "resetToken을 통한 비밀번호 재설정")
public class ResetPasswordController {

  private final PasswordChanger passwordChanger;

  @PostMapping("/reset-password")
  @Operation(
      summary = "비밀번호 재설정",
      description = "resetToken과 새 비밀번호를 입력받아 비밀번호를 변경합니다."
  )
  @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공")
  public ResponseEntity<String> resetPassword(
      @RequestBody @Valid PasswordChangeByTokenRequest request)
      throws Exception {
    passwordChanger.resetPassword(request);
    return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
  }
}
