package com.rocket.domains.posts.infrastructure.persistence.jpa;

import com.rocket.domains.posts.domain.entity.PostLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
  // 특정 사용자가 게시글에 좋아요를 눌렀는지 확인할 수 있다.
  Optional<PostLike> findByPostIdAndUserId(Long postId, Long UserId);

  // DB 기준 좋아요 수 집계
  Long countByPostId(Long postId);

}
