package com.rocket.domains.posts.domain.repository;

import java.util.Set;

public interface PostLikeCacheRepository {
  void incrementLikeCount(Long postId);
  int getLikeCount(Long postId);
  void resetLikeCount(Long postId);

  Set<Long> scanLikedPostIds();

  boolean hasKey(Long postId);

  void decrementLikeCount(Long postId);
}
