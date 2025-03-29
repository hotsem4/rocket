package com.rocket.domains.auth.domain.service;

import com.rocket.domains.auth.application.dto.response.AuthenticatedUser;
import com.rocket.domains.auth.application.dto.response.TokenResponse;
import com.rocket.domains.user.application.dto.request.UserRegisterRequest;
import com.rocket.domains.user.domain.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

  TokenResponse register(@Valid UserRegisterRequest dto);

  TokenResponse login(
      @NotBlank(message = "이메일은 필수 입력값입니다.")
      @Email(message = "올바른 이메일 형식이 아닙니다.")
      String email,

      @NotBlank(message = "비밀번호는 필수 입력값입니다.")
      String password
  );

  TokenResponse accessReissue(String refreshToken);

  TokenResponse refreshReissue(String email, Boolean shouldRefreshToken);

  void logout(String accessToken);

  AuthenticatedUser getAuthenticatedUser(String accessToken);

  void existsByEmail(String email);

  User authenticate(String email, String rawPassword);

}
