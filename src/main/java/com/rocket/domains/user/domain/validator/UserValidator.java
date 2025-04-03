package com.rocket.domains.user.domain.validator;

import com.rocket.commons.exception.exceptions.UserNotFoundException;
import com.rocket.domains.user.domain.repository.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

  private final UserReader userReader;

  public void validateUserExists(Long userId) {
    if (!userReader.existsById(userId)) {
      throw new UserNotFoundException(String.valueOf(userId));
    }
  }

  public void validateUserNicknameExists(String nickname) {
    if (nickname == null || nickname.isEmpty()) {
      throw new IllegalArgumentException("닉네임이 존재하지 않습니다.");
    }
    if (userReader.existsByNickname(nickname)) {
      throw new IllegalArgumentException("중복되는 닉네임이 존재합니다.");
    }
  }
}
