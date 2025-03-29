package com.rocket.domains.user.domain.service;

import com.rocket.domains.user.application.dto.request.UserRegisterRequest;
import com.rocket.domains.user.application.dto.request.UserUpdateRequest;
import com.rocket.domains.user.application.dto.response.UserInfoResponse;
import com.rocket.domains.user.domain.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

  User saveUser(UserRegisterRequest dto);

  UserInfoResponse findByEmail(String email);

  List<UserInfoResponse> findAllUsers();

  UserInfoResponse updateByEmail(UserUpdateRequest dto);

  boolean deleteByEmail(String email);

  Optional<User> findUserByEmail(String email);

  Boolean existsByEmail(String email);
}
