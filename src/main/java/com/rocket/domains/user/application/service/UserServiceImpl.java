package com.rocket.domains.user.application.service;

import com.rocket.commons.exception.exceptions.UserNotFoundException;
import com.rocket.domains.user.application.dto.request.UserRegisterRequest;
import com.rocket.domains.user.application.dto.request.UserUpdateRequest;
import com.rocket.domains.user.application.dto.response.UserInfoResponse;
import com.rocket.domains.user.domain.entity.User;
import com.rocket.domains.user.domain.repository.UserReader;
import com.rocket.domains.user.domain.repository.UserWriter;
import com.rocket.domains.user.domain.service.UserService;
import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

  private final UserReader userReader;
  private final UserWriter userWriter;
  private final PasswordEncoder passwordEncoder;

  public UserServiceImpl(UserReader userReader, UserWriter userWriter,
      PasswordEncoder passwordEncoder) {
    this.userReader = userReader;
    this.userWriter = userWriter;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  @Transactional
  public User saveUser(UserRegisterRequest dto) {
    String encodePassword = passwordEncoder.encode(dto.password());

    User user = UserMapper.toEntity(dto, encodePassword);
    return userWriter.saveUser(user);
  }

  @Override
  public UserInfoResponse findByEmail(String email) {
    User user = userReader.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException(email));
    return UserInfoResponse.fromUser(user);
  }

  @Override
  public List<UserInfoResponse> findAllUsers() {
    return userReader.findAll()
        .stream()
        .map(UserInfoResponse::fromUser)
        .toList();
  }

  @Override
  @Transactional
  public UserInfoResponse updateByEmail(UserUpdateRequest dto) {
    User user = userReader.findByEmail(dto.email())
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
    if (!userReader.existsByEmail(email)) {
      throw new UserNotFoundException(email);
    }

    return userWriter.deleteUser(email);
  }

  @Override
  public Optional<User> findUserByEmail(String email) {
    return userReader.findByEmail(email);
  }

  @Override
  public Boolean existsByEmail(String email) {
    return userReader.existsByEmail(email);
  }

}
