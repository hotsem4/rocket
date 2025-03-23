package com.rocket.domains.user.domain.service;

import com.rocket.domains.user.application.dto.request.UserRegisterRequest;
import com.rocket.domains.user.application.dto.request.UserUpdateRequest;
import com.rocket.domains.user.application.dto.response.UserInfoResponse;
import com.rocket.domains.user.domain.entity.User;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

  User saveUser(UserRegisterRequest dto);

  UserInfoResponse loginUser(String email, String password);

  UserInfoResponse findByEmail(String email);

  List<UserInfoResponse> findAllUsers();

  UserInfoResponse updateByEmail(UserUpdateRequest dto);

  boolean deleteByEmail(String email);

  User authenticate(String email, String rawPassword);
}
