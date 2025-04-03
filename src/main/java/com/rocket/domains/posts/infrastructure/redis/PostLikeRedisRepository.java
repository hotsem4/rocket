package com.rocket.domains.posts.infrastructure.redis;

import com.rocket.domains.posts.domain.repository.PostLikeCacheRepository;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeRedisRepository implements PostLikeCacheRepository {

  private final StringRedisTemplate redisTemplate;

  private static final String POST_LIKE_COUNT_KEY_PREFIX = "post:like:";
  private static final Long LIKE_KEY_TTL_MINUTES = 30L;

  /**
   * 좋아요 증가 - Redis에 키가 없으면 초기화 후 증가 및 TTL 설정 - 키가 있으면 INCR 후 TTL 갱신
   */
  public void incrementLikeCount(Long postId) {
    String key = POST_LIKE_COUNT_KEY_PREFIX + postId;
    Boolean exists = redisTemplate.hasKey(key);
    if (Boolean.FALSE.equals(exists)) {
      // 키 초기화
      redisTemplate.opsForValue().set(key, "0", LIKE_KEY_TTL_MINUTES, TimeUnit.MINUTES);
    }
    // 좋아요 수 증가
    redisTemplate.opsForValue().increment(key);
    // TTL 갱신
    refreshTTL(key);
  }

  /**
   * Redis에서 현재 좋아요 조회
   */
  public int getLikeCount(Long postId) {
    String key = POST_LIKE_COUNT_KEY_PREFIX + postId;
    String count = redisTemplate.opsForValue().get(key);
    return count != null ? Integer.parseInt(count) : 0;
  }

  /**
   * Redis에서 좋아요 키 삭제
   */
  public void resetLikeCount(Long postId) {
    String key = POST_LIKE_COUNT_KEY_PREFIX + postId;
    redisTemplate.delete(key);
  }

  @Override
  public Set<Long> scanLikedPostIds() {
    Set<Long> result = new HashSet<>();
    // match="post:like:*"로 SCAN
    ScanOptions options = ScanOptions.scanOptions().match(POST_LIKE_COUNT_KEY_PREFIX + "*").build();
    // Redis SCAN
    try (var cursor = redisTemplate.opsForValue().getOperations().scan(options)) {
      cursor.forEachRemaining(k -> {
        Long postId = parsePostId((String) k);
        result.add(postId);
      });
    }

    return result;
  }

  @Override
  public boolean hasKey(Long postId) {
    String key = POST_LIKE_COUNT_KEY_PREFIX + postId;
    Boolean exists = redisTemplate.hasKey(key);
    return Boolean.TRUE.equals(exists);
  }

  @Override
  public void decrementLikeCount(Long postId) {
    String key = POST_LIKE_COUNT_KEY_PREFIX + postId;

    if (!Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
      return;
    }
    String current = redisTemplate.opsForValue().get(key);
    if (current != null && Integer.parseInt(current) > 0) {
      redisTemplate.opsForValue().decrement(key);
    }
    refreshTTL(key);
  }

  private Long parsePostId(String key) {
    return Long.valueOf(key.substring(POST_LIKE_COUNT_KEY_PREFIX.length()));
  }

  private void refreshTTL(String key) {
    redisTemplate.expire(key, LIKE_KEY_TTL_MINUTES, TimeUnit.MINUTES);
  }
}
