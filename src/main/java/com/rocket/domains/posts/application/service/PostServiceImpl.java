package com.rocket.domains.posts.application.service;

import com.rocket.commons.exception.exceptions.PostNotFoundException;
import com.rocket.commons.exception.exceptions.UserNotFoundException;
import com.rocket.domains.posts.application.dto.request.PostCreateRequest;
import com.rocket.domains.posts.application.dto.request.PostUpdateRequest;
import com.rocket.domains.posts.application.dto.response.PostDetailInfoResponse;
import com.rocket.domains.posts.application.dto.response.PostListResponse;
import com.rocket.domains.posts.domain.entity.Post;
import com.rocket.domains.posts.domain.repository.PostRepository;
import com.rocket.domains.posts.domain.service.PostService;
import com.rocket.domains.user.domain.entity.User;
import com.rocket.domains.user.domain.repository.UserRepository;
import com.rocket.domains.user.domain.service.UserLookupService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

  private final PostRepository postRepository;
  private final UserLookupService userLookupService;
  private final UserRepository userRepository;

//  public PostServiceImpl(PostRepository postRepository, UserLookupService userLookupService) {
//    this.postRepository = postRepository;
//    this.userLookupService = userLookupService;
//  }

  @Override
  @Transactional
  public PostDetailInfoResponse savePost(PostCreateRequest dto, Long userId) {
    if (!userLookupService.existsById(userId)) {
      throw new IllegalArgumentException("유효하지 않은 작성자 ID입니다.");
    }

    if (dto.title() == null || dto.title().trim().isEmpty()) {
      throw new IllegalArgumentException("제목은 필수 입력값이며, 비어 있을 수 없습니다.");
    }
    if (dto.content() == null || dto.content().trim().isEmpty()) {
      throw new IllegalArgumentException("내용은 필수 입력값이며, 비어 있을 수 없습니다.");
    }

    User author = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("로그인 유저를 찾을 수 없습니다."));

    Post post = PostMapper.toEntity(dto, author);

    Post savedPost = postRepository.savePost(post);
    if (savedPost == null) {
      throw new IllegalArgumentException("게시글 저장 중 문제가 발생했습니다.");
    }

    return PostMapper.toDetailDto(savedPost);
  }


  @Override
  public List<PostListResponse> findByTitle(String title) {
    List<Post> posts = postRepository.findByTitle(title);
    return posts.stream().map(PostMapper::toListDto).collect(Collectors.toList());
  }

  @Override
  public List<PostListResponse> findAllPosts() {
    List<Post> posts = postRepository.findAll();
    return posts.stream().map(PostMapper::toListDto).collect(Collectors.toList());
  }

  @Override
  @Transactional
  public PostDetailInfoResponse updateById(Long id, PostUpdateRequest dto) {
    if ((dto.title() == null && dto.content() == null) ||
        (dto.title() != null && dto.title().trim().isEmpty() &&
            dto.content() != null && dto.content().trim().isEmpty())) {
      throw new IllegalArgumentException("변경할 값이 없습니다.");
    }

    boolean isUpdated = postRepository.updateById(id, dto.title(), dto.content());

    if (!isUpdated) {
      throw new IllegalArgumentException("게시글이 존재하지 않거나 업데이트할 수 없습니다.");
    }

    Post findByIdPost = postRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("데이터를 찾을 수 없습니다."));
    return PostMapper.toDetailDto(findByIdPost);
  }

  @Override
  public Boolean deleteById(Long id) {
    boolean isDeleted = postRepository.deleteById(id);

    if (!isDeleted) {
      throw new IllegalArgumentException("해당 ID의 게시글을 찾을 수 없습니다.");
    }

    return true;
  }


  @Override
  public PostDetailInfoResponse findById(Long id) {
    Post post = postRepository.findById(id)
        .orElseThrow(() -> new PostNotFoundException(String.valueOf(id)));
    return PostMapper.toDetailDto(post);
  }

  @Override
  @Transactional
  public PostDetailInfoResponse likeCountIncrement(Long id) {
    boolean updated = postRepository.incrementLikeCount(id);
    if (!updated) {
      throw new PostNotFoundException("게시글을 찾을 수 없습니다. ID: " + id);
    }
    Post updatedPost = postRepository.findById(id)
        .orElseThrow(() -> new PostNotFoundException("업데이트 후 게시글을 찾을 수 없습니다. ID: " + id));
    return PostMapper.toDetailDto(updatedPost);
  }

}
