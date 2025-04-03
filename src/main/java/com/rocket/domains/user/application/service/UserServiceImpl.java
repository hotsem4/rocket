package com.rocket.domains.user.application.service;

import com.rocket.commons.exception.exceptions.UserNotFoundException;
import com.rocket.domains.user.application.dto.request.UserRegisterRequest;
import com.rocket.domains.user.application.dto.request.UserUpdateRequest;
import com.rocket.domains.user.application.dto.response.UserInfoResponse;
import com.rocket.domains.user.application.dto.response.UserSimpleInfoResponse;
import com.rocket.domains.user.domain.entity.User;
import com.rocket.domains.user.domain.repository.UserReader;
import com.rocket.domains.user.domain.repository.UserWriter;
import com.rocket.domains.user.domain.service.UserService;
import com.rocket.domains.user.domain.validator.UserValidator;
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
  private final UserValidator userValidator;

  public UserServiceImpl(UserReader userReader, UserWriter userWriter,
      PasswordEncoder passwordEncoder, UserValidator userValidator) {
    this.userReader = userReader;
    this.userWriter = userWriter;
    this.passwordEncoder = passwordEncoder;
    this.userValidator = userValidator;
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

    if (dto.nickname() != null && !dto.nickname().equals(user.getNickname())) {
      userValidator.validateUserNicknameExists(dto.nickname());
    }

    user.updateFrom(dto);

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


  @Override
  public Optional<User> findUserById(Long userId) {
    return userReader.findById(userId);
  }

  @Override
  public Long findUserIdByEmailAndPhoneNumber(String email, String phoneNumber) {
    return userReader.findUserIdByEmailAndPhoneNumber(email, phoneNumber);
  }

  @Override
  public List<UserSimpleInfoResponse> findUserSimpleInfoList(List<Long> userIds) {
    List<User> users = userReader.findAllByIdIn(userIds);
    return users.stream()
        .map(UserMapper::toUserSimpleInfo)
        .toList();
  }

}
