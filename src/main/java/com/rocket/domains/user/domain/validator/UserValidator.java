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
}
