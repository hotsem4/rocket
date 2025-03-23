package com.rocket.domains.posts.infrastructure.persistence.impl;

import com.rocket.domains.posts.domain.entity.Post;
import com.rocket.domains.posts.domain.repository.PostReader;
import com.rocket.domains.posts.infrastructure.persistence.jpa.PostJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostReaderImpl implements PostReader {

  private final PostJpaRepository postJpaRepository;

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
  public boolean existsById(Long id) {
    return postJpaRepository.existsById(id);
  }
}
