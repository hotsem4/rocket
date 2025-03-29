package com.rocket.domains.user.application.facade;

import com.rocket.domains.user.application.dto.request.UserRegisterRequest;
import com.rocket.domains.user.domain.facade.UserFacade;
import com.rocket.domains.user.domain.entity.User;
import com.rocket.domains.user.domain.service.UserService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

  private final UserService userService;


  public User registerUser(UserRegisterRequest dto) {
    return userService.saveUser(dto);
  }

  public Optional<User> findUserByEmail(String email) {
    return userService.findUserByEmail(email);
  }

  public Boolean existsByEmail(String email) {
    return userService.existsByEmail(email);
  }
}
