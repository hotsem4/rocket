package com.rocket.domains.posts.infrastructure.persistence.impl;

import com.rocket.domains.posts.domain.entity.Post;
import com.rocket.domains.posts.domain.repository.PostWriter;
import com.rocket.domains.posts.infrastructure.persistence.jpa.PostJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostWriterImpl implements PostWriter {

  private final PostJpaRepository postJpaRepository;

  @Override
  public Post savePost(Post post) {
    return postJpaRepository.save(post);
  }

  @Override
  public void deleteById(Long id) {
    postJpaRepository.deleteById(id);
  }
}
