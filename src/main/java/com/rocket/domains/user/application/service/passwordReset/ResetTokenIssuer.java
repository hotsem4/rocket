package com.rocket.domains.user.application.service.passwordReset;

import com.rocket.commons.exception.exceptions.UserNotFoundException;
import com.rocket.commons.utils.ResetTokenUtil;
import com.rocket.domains.user.domain.entity.User;
import com.rocket.domains.user.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 해당 코드는 유저가 비밀번호를 변경하기 위해 email과 phoneNumber를 입력하면 실행되는 코드로 email을 통해 유저의 정보를 찾고 해당 유저가 존재한다면
 * token을 발급하고 resetTokenManager를 통해 토큰을 관리하고 return으로 사용자에게 암호화된 토큰을 전송한다.
 */
@Service
@RequiredArgsConstructor
public class ResetTokenIssuer {

  private final UserService userService;
  private final ResetTokenManager resetTokenManager;
  private final ResetTokenUtil resetTokenUtil;

  public String initiateReset(String email, String phoneNumber) throws Exception {
    User user = userService.findUserByEmail(email)
        .filter(u -> u.getPhoneNumber().equals(phoneNumber)) // phoneNumber 비교
        .orElseThrow(() -> new UserNotFoundException("해당 정보로 유저를 찾을 수 없습니다."));

    String token = resetTokenUtil.generateResetToken();
    resetTokenManager.storeToken(user.getId(), token);

    return resetTokenUtil.encrypt(token); // 암호화해서 사용자에게 전달
  }

  public String init(Long userId) throws Exception {
    String token = resetTokenUtil.generateResetToken();
    resetTokenManager.storeToken(userId, token);

    return resetTokenUtil.encrypt(token);
  }
}
