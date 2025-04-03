package com.rocket.domains.user.domain.facade;

import com.rocket.commons.exception.exceptions.UserNotFoundException;
import com.rocket.domains.user.application.dto.request.UserRegisterRequest;
import com.rocket.domains.user.application.dto.response.UserInfoResponse;
import com.rocket.domains.user.application.dto.response.UserSimpleInfoResponse;
import com.rocket.domains.user.domain.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserFacade {

  User registerUser(UserRegisterRequest dto);

  Optional<User> findUserByEmail(String email);

  Boolean existsByEmail(String email);

  Optional<User> findById(Long id);

  default User getByIdOrThrow(Long userId) {
    return findById(userId).orElseThrow(() -> new UserNotFoundException(String.valueOf(userId)));
  }

  List<UserSimpleInfoResponse> findUserInfoList(List<Long> userIds);
}
