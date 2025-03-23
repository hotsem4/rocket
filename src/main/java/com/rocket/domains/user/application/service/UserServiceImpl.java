package com.rocket.domains.user.application.service;

import com.rocket.commons.exception.exceptions.LoginFailedException;
import com.rocket.commons.exception.exceptions.UserNotFoundException;
import com.rocket.domains.user.application.dto.request.UserRegisterRequest;
import com.rocket.domains.user.application.dto.request.UserUpdateRequest;
import com.rocket.domains.user.application.dto.response.UserInfoResponse;
import com.rocket.domains.user.domain.entity.User;
import com.rocket.domains.user.domain.repository.UserRepository;
import com.rocket.domains.user.domain.service.UserService;
import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  @Transactional
  public User saveUser(UserRegisterRequest dto) {
    String encodePassword = passwordEncoder.encode(dto.password());

    User user = UserMapper.toEntity(dto, encodePassword);
    return userRepository.saveUser(user);
  }

  /**
   * Todo
   * JWT 토큰 발급 기능 추가 예정
   */
  @Override
  public UserInfoResponse loginUser(String email, String password) {

    User authenticatedUser = authenticate(email, password);
    return UserInfoResponse.fromUser(authenticatedUser);
  }

  @Override
  public User authenticate(String email, String rawPassword) {
    Optional<User> userOptional = userRepository.findByEmail(email);
    if (userOptional.isEmpty()) {
      passwordEncoder.matches(rawPassword, "dummyPassword");
      throw new LoginFailedException("이메일 또는 비밀번호가 올바르지 않습니다.");
    }

    User user = userOptional.get();

    if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
      throw new LoginFailedException("이메일 또는 비밀번호가 올바르지 않습니다.");
    }

    return user;
  }

  @Override
  public UserInfoResponse findByEmail(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException(email));
    return UserInfoResponse.fromUser(user);
  }

  /**
   * JdbcTemplate를 사용할 경우 paging기법과 offset을 사용해서 OOM을 방지해야 하지만 시간 관계상 구현하지 않음...
   */
  @Override
  public List<UserInfoResponse> findAllUsers() {
    return userRepository.findAll()
        .stream()
        .map(UserInfoResponse::fromUser)
        .toList();
  }

  @Override
  @Transactional
  public UserInfoResponse updateByEmail(UserUpdateRequest dto) {
    User user = userRepository.findByEmail(dto.email())
        .orElseThrow(() -> new UserNotFoundException(dto.email()));

    boolean isUpdated = false;

    if (dto.age() != null) {
      user.updateAge(dto.age());
      isUpdated = true;
    }
    if (dto.gender() != null) {
      user.updateGender(dto.gender());
      isUpdated = true;
    }
    if (dto.address() != null) {
      user.updateAddress(dto.address());
      isUpdated = true;
    }
    if (dto.nickname() != null) {
      user.updateNickname(dto.nickname());
      isUpdated = true;
    }

    if (!isUpdated) {
      throw new IllegalArgumentException("변경된 내용이 없습니다.");
    }

    return UserInfoResponse.fromUser(user);
  }

  @Override
  public boolean deleteByEmail(String email) {
    if (!userRepository.existsByEmail(email)) {
      throw new UserNotFoundException(email);
    }

    return userRepository.deleteUser(email);
  }


}
