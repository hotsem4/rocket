package com.rocket.domains.posts.infrastructure.persistence;

import com.rocket.domains.posts.domain.entity.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostJpaRepository extends JpaRepository<Post, Long> {

  // 제목으로 검색하기(search)
  List<Post> findByTitle(String title);

  // 유저의 ID로 조회하기
  List<Post> findAllByAuthorId(Long userId);

  Boolean updateById(Long postId, String title, String content);

  Boolean incrementLikeCount(Long postId);
}
