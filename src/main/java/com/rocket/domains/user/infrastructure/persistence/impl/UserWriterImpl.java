package com.rocket.domains.user.infrastructure.persistence.impl;

import com.rocket.domains.user.domain.entity.User;
import com.rocket.domains.user.domain.repository.UserWriter;
import com.rocket.domains.user.infrastructure.persistence.jpa.UserJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class UserWriterImpl implements UserWriter {

  private final UserJpaRepository userJpaRepository;

  public UserWriterImpl(UserJpaRepository userJpaRepository) {
    this.userJpaRepository = userJpaRepository;
  }

  @Override
  public User saveUser(User user) {
    return userJpaRepository.save(user);
  }

  @Override
  public Boolean deleteUser(String email) {
    return userJpaRepository.deleteByEmail(email);
  }
}
