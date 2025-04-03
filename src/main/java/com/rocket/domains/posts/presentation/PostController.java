package com.rocket.domains.posts.presentation;

import com.rocket.commons.security.auth.CustomUserDetails;
import com.rocket.domains.posts.application.dto.request.PostCreateRequest;
import com.rocket.domains.posts.application.dto.request.PostUpdateRequest;
import com.rocket.domains.posts.application.dto.response.PostDetailInfoResponse;
import com.rocket.domains.posts.application.dto.response.PostListResponse;
import com.rocket.domains.posts.application.service.PostLikeService;
import com.rocket.domains.posts.domain.service.PostService;
import com.rocket.domains.user.application.dto.response.UserSimpleInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
@Tag(name = "Post Controller", description = "게시글 관련 API")
public class PostController {

  private final PostService postService;
  private final PostLikeService postLikeService;

  public PostController(PostService postService, PostLikeService postLikeService) {
    this.postLikeService = postLikeService;
    this.postService = postService;
  }

  @PostMapping("/create")
  @Operation(summary = "게시글 작성", description = "새로운 게시글을 작성합니다.")
  public ResponseEntity<PostDetailInfoResponse> createPost(
      @Valid @RequestBody PostCreateRequest dto,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {
    PostDetailInfoResponse savedPost = postService.savePost(dto, userDetails.id());
    return ResponseEntity.ok(savedPost);
  }

  @GetMapping
  @Operation(summary = "게시글 목록 조회", description = "전체 게시글 목록을 조회합니다.")
  public ResponseEntity<List<PostListResponse>> findAllPosts() {
    List<PostListResponse> posts = postService.findAllPosts();
    return ResponseEntity.ok(posts);
  }

  @GetMapping("/{id}")
  @Operation(summary = "게시글 상세 조회", description = "게시글 ID를 기반으로 게시글 상세 정보를 조회합니다.")
  public ResponseEntity<PostDetailInfoResponse> findById(@PathVariable Long id) {
    PostDetailInfoResponse resultPost = postService.findById(id);
    return ResponseEntity.ok(resultPost);
  }

  @PutMapping("/{id}")
  @Operation(summary = "게시글 수정", description = "게시글 ID를 기반으로 제목과 내용을 수정합니다.")
  public ResponseEntity<PostDetailInfoResponse> updatePost(
      @PathVariable Long id,
      @Valid @RequestBody PostUpdateRequest dto,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {

    PostDetailInfoResponse updatedPost = postService.updateById(id, dto, userDetails.id());
    return ResponseEntity.ok(updatedPost);

  }

  @DeleteMapping("/{id}")
  @Operation(summary = "게시글 삭제", description = "게시글 ID를 기반으로 게시글을 삭제합니다.")
  public ResponseEntity<Void> deletePost(
      @PathVariable Long id,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {
    postService.deleteById(id, userDetails.id());
    return ResponseEntity.noContent().build(); // 예외가 없으면 무조건 성공
  }

  @PutMapping("/{postId}/like")
  @Operation(summary = "게시글 좋아요 증가", description = "게시글 ID를 기반으로 좋아요 수를 1 증가시킵니다.")
  public ResponseEntity<PostDetailInfoResponse> likeCountIncrement(
      @PathVariable Long postId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    postLikeService.likePost(postId, userDetails.id());
    return ResponseEntity.ok(postService.getPostDetailWithLikes(postId));
  }

  @PutMapping("/{postId}/unlike")
  @Operation(summary = "좋아요 취소")
  public ResponseEntity<PostDetailInfoResponse> unlikePost(
      @PathVariable Long postId,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {
    postLikeService.unlikePost(postId, userDetails.id());
    return ResponseEntity.ok(postService.getPostDetailWithLikes(postId));
  }

  @GetMapping("/{postId}/likes")
  @Operation(summary = "게시글 좋아요 유저 목록", description = "게시글을 좋아요한 유저들의 간단한 정보를 반환합니다.")
  public ResponseEntity<List<UserSimpleInfoResponse>> getLikedUsers(@PathVariable Long postId) {
    List<UserSimpleInfoResponse> userList = postLikeService.getUsersWhoLikedPost(postId);
    return ResponseEntity.ok(userList);
  }

}
