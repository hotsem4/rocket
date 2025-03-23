package com.rocket.domains.posts.application.service;

import com.rocket.commons.exception.exceptions.DuplicateLikeException;
import com.rocket.commons.exception.exceptions.PostNotFoundException;
import com.rocket.domains.posts.domain.entity.PostLike;
import com.rocket.domains.posts.domain.repository.PostReader;
import com.rocket.domains.posts.infrastructure.persistence.jpa.PostLikeRepository;
import com.rocket.domains.posts.infrastructure.redis.PostLikeRedisService;
import com.rocket.domains.user.domain.validator.UserValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeService {

  private final UserValidator userValidator;
  private final PostLikeRepository postLikeRepository;
  private final PostReader postReader;
  private final PostLikeRedisService postLikeRedisService;

  /**
   * 좋아요 요청 처리: 사용자와 게시글의 좋아요 중복 여부 확인 후, DB에 좋아요 기록 저장 및 Redis에 카운트 증가 처리
   */
  @Transactional
  public void likePost(Long postId, Long userId) {
    // 이미 좋아요를 누른 적이 있는지 확인
    postLikeRepository.findByPostIdAndUserId(postId, userId)
        .ifPresent(like -> {
          throw new DuplicateLikeException("이미 좋아요를 누르셨습니다.");
        });

    // 사용자와 게시글 확인하기
    if (!postReader.existsById(postId)) {
      throw new PostNotFoundException(String.valueOf(postId));
    }
    userValidator.validateUserExists(userId);

    // DB에 좋아요 기록 저장
    PostLike postLike = new PostLike(postId, userId);
    postLikeRepository.save(postLike);

    // Redis에 좋아요 수 증가(TTL 적용)
    postLikeRedisService.incrementLikeCount(postId);
  }
}
