package com.rocket.domains.user.application.service.passwordReset;

import com.rocket.domains.user.domain.repository.VerificationStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerificationService {

  private final VerificationStore verificationStore;

  public void verify(Long userId) {
    verificationStore.storeVerification(userId);
  }

  public boolean isVerified(Long userId) {
    return verificationStore.isVerified(userId);
  }

  public void clearVerification(Long userId) {
    verificationStore.deleteVerification(userId);
  }
}
