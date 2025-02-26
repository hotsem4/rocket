package com.rocket.domains.posts.domain.service;

import com.rocket.domains.posts.application.dto.common.PostDTO;
import com.rocket.domains.posts.application.dto.request.PostUpdateDTO;
import com.rocket.domains.posts.application.dto.response.PostInfoDTO;
import com.rocket.domains.posts.domain.entity.Post;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public interface PostService {
  PostInfoDTO savePost(PostDTO post);

  List<PostDTO> findByTitle(String title);

  List<PostDTO> findAllPosts();

  PostInfoDTO updateById(Long id, PostUpdateDTO dto);

  boolean deleteById(Long id);

  PostInfoDTO findById(Long id);
}
