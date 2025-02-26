package com.rocket.domains.user.application.service;

import com.rocket.domains.user.domain.repository.UserRepository;
import com.rocket.domains.user.domain.service.UserLookupService;
import org.springframework.stereotype.Service;

@Service
public class UserLookupServiceImpl implements UserLookupService {

  private final UserRepository userRepository;

  public UserLookupServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public boolean existsById(Long id) {
    return userRepository.existsById(id);
  }

  @Override
  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }
}
