package com.rocket.domains.user.infrastructure.persistence.redis;

import com.rocket.domains.user.domain.repository.VerificationStore;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisVerificationStore implements VerificationStore {

  private final RedisTemplate<String, String> redisTemplate;
  private final static Duration TTL = Duration.ofMinutes(5);

  private String getKey(Long userId) {
    return "password-change-verified:" + userId;
  }

  @Override
  public void storeVerification(Long userId) {
    redisTemplate.opsForValue().set(getKey(userId), "true", TTL);
  }

  @Override
  public boolean isVerified(Long userId) {
    return "true".equals(redisTemplate.opsForValue().get(getKey(userId)));
  }

  @Override
  public void deleteVerification(Long userId) {
    redisTemplate.delete(getKey(userId));
  }
}
