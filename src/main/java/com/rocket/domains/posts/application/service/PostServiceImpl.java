package com.rocket.domains.posts.application.service;

import com.rocket.commons.exception.exceptions.AccessDeniedCustomException;
import com.rocket.commons.exception.exceptions.PostNotFoundException;
import com.rocket.commons.exception.exceptions.UserNotFoundException;
import com.rocket.domains.posts.application.assembler.PostResponseAssembler;
import com.rocket.domains.posts.application.dto.request.PostCreateRequest;
import com.rocket.domains.posts.application.dto.request.PostUpdateRequest;
import com.rocket.domains.posts.application.dto.response.PostDetailInfoResponse;
import com.rocket.domains.posts.application.dto.response.PostListResponse;
import com.rocket.domains.posts.domain.entity.Post;
import com.rocket.domains.posts.domain.repository.PostReader;
import com.rocket.domains.posts.domain.repository.PostWriter;
import com.rocket.domains.posts.domain.service.PostService;
import com.rocket.domains.user.domain.entity.User;
import com.rocket.domains.user.domain.facade.UserFacade;
import com.rocket.domains.user.domain.service.UserLookupService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

  private final UserLookupService userLookupService;
  private final PostReader postReader;
  private final PostWriter postWriter;
  private final PostResponseAssembler postResponseAssembler;
  private final UserFacade userFacade;

  @Override
  @Transactional
  public PostDetailInfoResponse savePost(PostCreateRequest dto, Long userId) {

    if (!userLookupService.existsById(userId)) {
      throw new IllegalArgumentException("유효하지 않은 작성자 ID입니다.");
    }

    User author = userLookupService.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("로그인 유저를 찾을 수 없습니다."));

    Post post = PostMapper.toEntity(dto, author);


    Post savedPost = postWriter.savePost(post);
    if (savedPost == null) {
      throw new IllegalArgumentException("게시글 저장 중 문제가 발생했습니다.");
    }

    return postResponseAssembler.toDetailDto(savedPost);
  }


  @Override
  public List<PostListResponse> findByTitle(String title) {
    List<Post> posts = postReader.findByTitle(title);
    return posts.stream().map(PostMapper::toListDto).collect(Collectors.toList());
  }

  @Override
  public List<PostListResponse> findAllPosts() {
    List<Post> posts = postReader.findAll();
    return posts.stream().map(PostMapper::toListDto).collect(Collectors.toList());
  }

  @Override
  @Transactional
  public PostDetailInfoResponse updateById(Long id, PostUpdateRequest dto, Long userId) {
    Post post = postReader.findById(id)
        .orElseThrow(() -> new PostNotFoundException(String.valueOf(id)));

    User user = userFacade.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(String.valueOf(userId)));

    if (!post.isOwnedBy(user) && !user.isAdmin()) {
      throw new AccessDeniedCustomException("게시글 수정 권한이 없습니다.");
    }

    post.updateFrom(dto); // 핵심 비즈니스 로직은 도메인 객체가 책임짐

    return postResponseAssembler.toDetailDto(post);
  }

  @Override
  public void deleteById(Long id, Long userId) {
    Post post = postReader.findById(id)
        .orElseThrow(() -> new PostNotFoundException(String.valueOf(id)));

    User user = userFacade.getByIdOrThrow(userId);

    if (!post.isOwnedBy(user) && !user.isAdmin()){
      throw new AccessDeniedCustomException("게시글을 삭제할 권한이 없습니다.");
    }

    postWriter.deleteById(id);
  }


  @Override
  public PostDetailInfoResponse findById(Long id) {
//    PostRepository postRepository = postRepositoryProvider.getIfAvailable();
    Post post = postReader.findById(id)
        .orElseThrow(() -> new PostNotFoundException(String.valueOf(id)));
    return postResponseAssembler.toDetailDto(post);
  }

  @Override
  public PostDetailInfoResponse getPostDetailWithLikes(Long postId) {
    Post post = postReader.findById(postId)
        .orElseThrow(() -> new PostNotFoundException(String.valueOf(postId)));
    return postResponseAssembler.toDetailDto(post);
  }

}
