package com.rocket.domains.posts.application.service;

import com.rocket.commons.exception.exceptions.DuplicateLikeException;
import com.rocket.commons.exception.exceptions.PostNotFoundException;
import com.rocket.commons.exception.exceptions.LikeNotFoundException;
import com.rocket.domains.posts.domain.entity.Post;
import com.rocket.domains.posts.domain.entity.PostLike;
import com.rocket.domains.posts.domain.repository.PostLikeCacheRepository;
import com.rocket.domains.posts.domain.repository.PostReader;
import com.rocket.domains.posts.domain.repository.PostWriter;
import com.rocket.domains.posts.infrastructure.persistence.jpa.PostLikeRepository;
import com.rocket.domains.user.application.dto.response.UserInfoResponse;
import com.rocket.domains.user.application.dto.response.UserSimpleInfoResponse;
import com.rocket.domains.user.domain.facade.UserFacade;
import com.rocket.domains.user.domain.validator.UserValidator;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class PostLikeService {

  private final UserValidator userValidator;
  private final PostLikeRepository postLikeRepository;    // DB
  private final PostReader postReader;                    // DB
  private final PostLikeCacheRepository cacheRepo;    // Redis
  private final UserFacade userFacade;
  private final PostWriter postWriter;

  @Transactional
  public void likePost(Long postId, Long userId) {
    postLikeRepository.findByPostIdAndUserId(postId, userId).ifPresent(like -> {
      throw new DuplicateLikeException("이미 해당 게시물에 좋아요을 눌렀습니다.");
    });

    // 게시글이 실제 DB에 존재하는가?
    if (!postReader.existsById(postId)) {
      throw new PostNotFoundException(String.valueOf(postId));
    }

    // 사용자 유효성 검사
    userValidator.validateUserExists(userId);

    postLikeRepository.save(new PostLike(postId, userId));
    cacheRepo.incrementLikeCount(postId);
  }

  @Transactional
  public void unlikePost(Long postId, Long userId) {
    // 1. 좋아요 기록이 DB에 있는지 확인
    PostLike postLike = postLikeRepository.findByPostIdAndUserId(postId, userId)
        .orElseThrow(() -> new LikeNotFoundException("해당 게시물에는 좋아요를 누르지 않았습니다."));

    postLikeRepository.delete(postLike);

    // 2. Redis에 키가 있으면 -1, 없으면 DB에서 -1
    if (cacheRepo.hasKey(postId)) {
      cacheRepo.decrementLikeCount(postId);
    } else {
      Post post = postReader.findById(postId).orElseThrow(() -> new PostNotFoundException(String.valueOf(postId)));
      post.addLikeCount(-1);
    }
  }

  @Transactional(readOnly = true)
  public int getRedisLikeCount(Long id) {
    return cacheRepo.getLikeCount(id);
  }

  @Transactional
  public void syncRedisLikeToDB() {
    Set<Long> postIds = cacheRepo.scanLikedPostIds();
    if (postIds.isEmpty()) {
      log.info("[syncRedisLikeToDB] No posts found in Redis");
      return; // 좋아요 발생한 게시글 없음.
    }

    // 각 postId에 대해 DB에 합산
    for (Long postId : postIds) {
      int redisCount = cacheRepo.getLikeCount(postId);

      log.info("[syncRedisLikeToDB] postId: {}, redisCount: {}", postId, redisCount);


      if (redisCount <= 0) {
        log.info("[syncRedisLikeToDB] Skipping postId: {} due to ttl or zero count", postId);

        continue;
      }

      Post post = postReader.findById(postId).orElse(null);
      if (post != null) {
        post.addLikeCount(redisCount);
        log.info("[syncRedisLikeToDB] Before Save - postId: {}, updatedLikeCount: {}", postId, post.getLikeCount());

        postWriter.savePost(post);
      } else {
        log.warn("[syncRedisLikeToDB] Post not found for postId: {}", postId);

      }

      cacheRepo.resetLikeCount(postId);
    }
  }

  @Transactional(readOnly = true)
  public List<UserSimpleInfoResponse> getUsersWhoLikedPost(Long postId) {
    List<PostLike> likes = postLikeRepository.findAllByPostId(postId);
    List<Long> userIds = likes.stream()
        .map(PostLike::getUserId)
        .toList();

    return userFacade.findUserInfoList(userIds);
  }

}
