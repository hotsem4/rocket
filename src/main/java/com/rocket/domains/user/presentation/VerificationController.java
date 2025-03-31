package com.rocket.domains.user.presentation;

import com.rocket.domains.user.application.dto.request.PasswordResetTokenRequest;
import com.rocket.domains.user.application.service.passwordReset.ResetTokenIssuer;
import com.rocket.domains.user.application.service.passwordReset.VerificationService;
import com.rocket.domains.user.domain.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Log4j2
@RequiredArgsConstructor
@Tag(name = "Password Verification", description = "비밀번호 변경을 위한 휴대폰 인증 및 resetToken 발급")
public class VerificationController {

  private final VerificationService verificationService;
  private final UserService userService;
  private final ResetTokenIssuer resetTokenIssuer;

  @PostMapping("/verify-phone")
  @Operation(
      summary = "휴대폰 인증 + resetToken 발급",
      description = "이메일과 전화번호를 통해 사용자를 인증하고, resetToken을 발급합니다."
  )
  @ApiResponse(responseCode = "200", description = "인증 성공 및 토큰 발급")
  public ResponseEntity<Map<String, String>> verifyPhone(
      @RequestBody PasswordResetTokenRequest request)
      throws Exception {
    Long userId = userService.findUserIdByEmailAndPhoneNumber(request.email(),
        request.phoneNumber());
    verificationService.verify(userId); // 내부에서 Redis에 인증 성공 처리

    String encryptedToken = resetTokenIssuer.init(userId);
    log.info("비밀번호 재설정 토큰 발급 완료. email={} phone={}", request.email(), request.phoneNumber());

    Map<String, String> response = Map.of(
        "message", "휴대폰 인증이 완료되었습니다.",
        "resetToken", encryptedToken
    );

    return ResponseEntity.ok(response);
  }
}
