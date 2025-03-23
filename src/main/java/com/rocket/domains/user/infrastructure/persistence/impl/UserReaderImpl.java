package com.rocket.domains.user.infrastructure.persistence.impl;

import com.rocket.domains.user.domain.entity.User;
import com.rocket.domains.user.domain.repository.UserReader;
import com.rocket.domains.user.infrastructure.persistence.jpa.UserJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class UserReaderImpl implements UserReader {

  private final UserJpaRepository userJpaRepository;

  public UserReaderImpl(UserJpaRepository userJpaRepository) {
    this.userJpaRepository = userJpaRepository;
  }

  @Override
  public Boolean existsById(Long id) {
    return userJpaRepository.existsById(id);
  }

  @Override
  public Optional<User> findById(Long id) {
    return userJpaRepository.findById(id);
  }

  @Override
  public Boolean existsByEmail(String email) {
    return userJpaRepository.existsByEmail(email);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return userJpaRepository.findByEmail(email);
  }

  @Override
  public Boolean existsByNickname(String nickname) {
    return userJpaRepository.existsByNickname(nickname);
  }

  @Override
  public List<User> findAll() {
    return userJpaRepository.findAll();
  }
}
