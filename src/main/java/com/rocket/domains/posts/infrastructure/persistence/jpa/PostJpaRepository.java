package com.rocket.domains.posts.infrastructure.persistence.jpa;

import com.rocket.domains.posts.domain.entity.Post;
import java.util.List;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostJpaRepository extends JpaRepository<Post, Long> {

  // 제목으로 검색하기(search)
  List<Post> findByTitle(String title);

  // TODO pagenamtion 구현할 것
  // 유저의 ID로 조회하기(paginatino을 위해 별도 생성)
  List<Post> findAllByAuthorId(Long userId);

  void deleteById(@NonNull Long postId);
}
