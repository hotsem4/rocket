package com.rocket.domains.posts.domain.enums.repository;

import com.rocket.domains.posts.domain.entity.Post;
import java.util.List;
import java.util.Optional;

public interface PostRepository {
  Post savePost(Post post);

  Optional<Post> findById(Long id);

  List<Post> findByTitle(String title);

  List<Post> findAll();

  /**
   * API 기반이기 때문에 사용자 인터페이스에서 삭제버튼을 클릭하면 ID를 알 수 있을 것이다.
   * @param id
   * @return
   */
  Boolean deleteById(Long id);

  Boolean updateById(Long id, String title, String content);
}
