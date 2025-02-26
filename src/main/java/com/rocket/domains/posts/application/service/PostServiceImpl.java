package com.rocket.domains.posts.application.service;

import com.rocket.commons.exception.exceptions.PostNotFoundException;
import com.rocket.domains.posts.application.dto.common.PostDTO;
import com.rocket.domains.posts.application.dto.request.PostUpdateDTO;
import com.rocket.domains.posts.application.dto.response.PostInfoDTO;
import com.rocket.domains.posts.domain.entity.Post;
import com.rocket.domains.posts.domain.enums.repository.PostRepository;
import com.rocket.domains.posts.domain.service.PostService;
import com.rocket.domains.user.domain.service.UserLookupService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {

  private final PostRepository postRepository;
  private final UserLookupService userLookupService;

  public PostServiceImpl(PostRepository postRepository, UserLookupService userLookupService) {
    this.postRepository = postRepository;
    this.userLookupService = userLookupService;
  }

  @Override
  public PostInfoDTO savePost(PostDTO dto) {
    if (!userLookupService.existsById(dto.authorId())) {
      throw new IllegalArgumentException("유효하지 않은 작성자 ID입니다.");
    }

    if (dto.title() == null || dto.title().trim().isEmpty()) {
      throw new IllegalArgumentException("제목은 필수 입력값이며, 비어 있을 수 없습니다.");
    }
    if (dto.content() == null || dto.content().trim().isEmpty()) {
      throw new IllegalArgumentException("내용은 필수 입력값이며, 비어 있을 수 없습니다.");
    }

    Post post = dto.toEntity();

    Post savedPost = postRepository.savePost(post);
    if (savedPost == null) {
      throw new IllegalArgumentException("게시글 저장 중 문제가 발생했습니다.");
    }

    return PostInfoDTO.from(savedPost);
  }


  @Override
  public List<PostDTO> findByTitle(String title) {
    List<Post> posts = postRepository.findByTitle(title);
    return posts.stream().map(PostDTO::from).collect(Collectors.toList());
  }

  @Override
  public List<PostDTO> findAllPosts() {
    List<Post> posts = postRepository.findAll();
    return posts.stream().map(PostDTO::from).collect(Collectors.toList());
  }

  @Override
  public PostInfoDTO updateById(Long id, PostUpdateDTO dto) {
    if ((dto.title() == null && dto.content() == null) ||
        (dto.title() != null && dto.title().trim().isEmpty() &&
            dto.content() != null && dto.content().trim().isEmpty())) {
      throw new IllegalArgumentException("변경할 값이 없습니다.");
    }

    boolean isUpdated = postRepository.updateById(id, dto.title(), dto.content());

    if (!isUpdated) {
      throw new IllegalArgumentException("게시글이 존재하지 않거나 업데이트할 수 없습니다.");
    }

    Post findByIdPost = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("데이터를 찾을 수 없습니다."));
    return PostInfoDTO.from(findByIdPost);
  }

  @Override
  public boolean deleteById(Long id) {
    boolean isDeleted = postRepository.deleteById(id);

    if (!isDeleted) {
      throw new IllegalArgumentException("해당 ID의 게시글을 찾을 수 없습니다.");
    }

    return true;
  }


  @Override
  public PostInfoDTO findById(Long id) {
    Post post = postRepository.findById(id)
        .orElseThrow(() -> new PostNotFoundException(String.valueOf(id)));
    return PostInfoDTO.from(post);

  }
}
