package com.rocket.domains.user.domain.repository;

public interface VerificationStore {

  void storeVerification(Long userId);

  boolean isVerified(Long userId);

  void deleteVerification(Long userId);
}
