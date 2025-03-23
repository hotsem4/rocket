package com.rocket.domains.user.infrastructure.persistence.impl;

import com.rocket.domains.user.domain.entity.User;
import com.rocket.domains.user.domain.repository.UserRepository;
import com.rocket.domains.user.infrastructure.persistence.jpa.UserJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class UserJpaRepositoryImpl implements UserRepository {

  private final UserJpaRepository userJpaRepository;

  public UserJpaRepositoryImpl(UserJpaRepository userJpaRepository) {
    this.userJpaRepository = userJpaRepository;
  }

  @Override
  public User saveUser(User user) {
    return userJpaRepository.save(user);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return userJpaRepository.findByEmail(email);
  }

  @Override
  public List<User> findAll() {
    return userJpaRepository.findAll();
  }

  @Override
  public Boolean deleteUser(String email) {
    return userJpaRepository.deleteByEmail(email);
  }

  @Override
  public Boolean existsByEmail(String email) {
    return userJpaRepository.existsByEmail(email);
  }

  @Override
  public Boolean existsById(Long id) {
    return userJpaRepository.existsById(id);
  }

  @Override
  public Boolean existsByNickname(String nickname) {
    return userJpaRepository.existsByNickname(nickname);
  }

  @Override
  public Optional<User> findById(Long userId) {
    return userJpaRepository.findById(userId);
  }
}
