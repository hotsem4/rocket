package com.rocket.domains.user.application.service.passwordReset;

import com.rocket.commons.exception.exceptions.UserNotFoundException;
import com.rocket.domains.user.application.dto.request.PasswordChangeByTokenRequest;
import com.rocket.domains.user.domain.entity.User;
import com.rocket.domains.user.domain.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 해당 클래스는 인증 이후 사용되며 resetToken, newPassword를 전달받아 새로운 비밀번호는 User에 삽입하고 기존 발급되었던 resetToken을 삭제하는
 * 코드입니다.
 */
@Service
@RequiredArgsConstructor
public class PasswordChanger {

  private final ResetTokenManager tokenStore;
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final VerificationService verificationService;

  public void resetPassword(@Valid PasswordChangeByTokenRequest request) throws Exception {
    Long userId = tokenStore.validateToken(request.resetToken());
    boolean verified = verificationService.isVerified(userId);
    if (!verified) {
      throw new IllegalArgumentException("휴대폰 인증이 완료되지 않았습니다.");
    }

    verificationService.clearVerification(userId); // 인증 완료 후 즉시 삭제

    User user = userService.findUserById(userId)
        .orElseThrow(() -> new UserNotFoundException("해당 유저를 찾을 수 없습니다."));

    String encryptedPassword = passwordEncoder.encode(request.newPassword());
    user.updatePassword(encryptedPassword); // DB에 password 저장 시 암호화하여 저장
  }
}
