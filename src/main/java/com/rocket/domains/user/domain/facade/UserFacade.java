package com.rocket.domains.user.domain.facade;

import com.rocket.domains.user.application.dto.request.UserRegisterRequest;
import com.rocket.domains.user.domain.entity.User;
import java.util.Optional;

public interface UserFacade {

  User registerUser(UserRegisterRequest dto);

  Optional<User> findUserByEmail(String email);

  Boolean existsByEmail(String email);
}
