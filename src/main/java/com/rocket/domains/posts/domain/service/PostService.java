package com.rocket.domains.posts.domain.service;

import com.rocket.domains.posts.application.dto.request.PostCreateRequest;
import com.rocket.domains.posts.application.dto.request.PostUpdateRequest;
import com.rocket.domains.posts.application.dto.response.PostDetailInfoResponse;
import com.rocket.domains.posts.application.dto.response.PostListResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface PostService {

  PostDetailInfoResponse savePost(PostCreateRequest dto, Long userId);

  List<PostListResponse> findByTitle(String title);

  List<PostListResponse> findAllPosts();

  PostDetailInfoResponse updateById(Long id, PostUpdateRequest dto);

  Boolean deleteById(Long id);

  PostDetailInfoResponse findById(Long id);

  PostDetailInfoResponse likeCountIncrement(Long id);
}
