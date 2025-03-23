package com.rocket.domains.posts.infrastructure.persistence.impl;

import com.rocket.domains.posts.domain.entity.Post;
import com.rocket.domains.posts.domain.repository.PostRepository;
import com.rocket.domains.posts.infrastructure.persistence.jpa.PostJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostJpaRepositoryImpl implements PostRepository {

  private final PostJpaRepository postJpaRepository;

  @Override
  public Post savePost(Post post) {
    postJpaRepository.save(post);
    return post;
  }

  @Override
  public Optional<Post> findById(Long id) {
    return postJpaRepository.findById(id);
  }

  @Override
  public List<Post> findByTitle(String title) {
    return postJpaRepository.findByTitle(title);
  }

  @Override
  public List<Post> findAll() {
    return postJpaRepository.findAll();
  }

  @Override
  public void deleteById(Long id) {
    postJpaRepository.deleteById(id);
  }

  @Override
  public Boolean incrementLikeCount(Long id) {
    // TODO Redis를 이용한 구현
    return null;
  }
}
