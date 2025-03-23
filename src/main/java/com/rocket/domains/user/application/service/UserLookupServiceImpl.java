package com.rocket.domains.user.application.service;

import com.rocket.domains.user.domain.entity.User;
import com.rocket.domains.user.domain.repository.UserReader;
import com.rocket.domains.user.domain.service.UserLookupService;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserLookupServiceImpl implements UserLookupService {

  private final UserReader userReader;

  public UserLookupServiceImpl(UserReader userReader) {
    this.userReader = userReader;
  }

  @Override
  public Boolean existsById(Long id) {
    return userReader.existsById(id);
  }

  @Override
  public Optional<User> findById(Long id) {
    return userReader.findById(id);
  }
}
