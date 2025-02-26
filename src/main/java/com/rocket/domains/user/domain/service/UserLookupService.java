package com.rocket.domains.user.domain.service;

public interface UserLookupService {
  boolean existsById(Long id);
  boolean existsByEmail(String email);
}
