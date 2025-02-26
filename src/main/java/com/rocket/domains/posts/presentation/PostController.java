package com.rocket.domains.posts.presentation;

import com.rocket.domains.posts.application.dto.common.PostDTO;
import com.rocket.domains.posts.application.dto.request.PostUpdateDTO;
import com.rocket.domains.posts.application.dto.response.PostInfoDTO;
import com.rocket.domains.posts.domain.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
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

  public PostController(PostService postService) {
    this.postService = postService;
  }

  @PostMapping("/save")
  @Operation(summary = "게시글 작성", description = "새로운 게시글을 작성합니다.")
  public ResponseEntity<PostInfoDTO> createPost(@Valid @RequestBody PostDTO post) {
    PostInfoDTO savedPost = postService.savePost(post);
    return ResponseEntity.ok(savedPost);
  }

  @GetMapping
  @Operation(summary = "게시글 목록 조회", description = "전체 게시글 목록을 조회합니다.")
  public ResponseEntity<List<PostDTO>> findAllPosts() {
    List<PostDTO> posts = postService.findAllPosts();
    return ResponseEntity.ok(posts);
  }

  @GetMapping("/{id}")
  @Operation(summary = "게시글 상세 조회", description = "게시글 ID를 기반으로 게시글 상세 정보를 조회합니다.")
  public ResponseEntity<PostInfoDTO> findById(@PathVariable Long id) {
    PostInfoDTO resultPost = postService.findById(id);
    return ResponseEntity.ok(resultPost);
  }

  @PutMapping("/{id}")
  @Operation(summary = "게시글 수정", description = "게시글 ID를 기반으로 제목과 내용을 수정합니다.")
  public ResponseEntity<PostInfoDTO> updatePost(@PathVariable Long id, @Valid @RequestBody PostUpdateDTO dto) {
    PostInfoDTO updatedPost = postService.updateById(id, dto);
    return ResponseEntity.ok(updatedPost);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "게시글 삭제", description = "게시글 ID를 기반으로 게시글을 삭제합니다.")
  public ResponseEntity<Void> deletePost(@PathVariable Long id) {
    boolean isDeleted = postService.deleteById(id);
    if (isDeleted) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @PutMapping("/{id}/like")
  @Operation(summary = "게시글 좋아요 증가", description = "게시글 ID를 기반으로 좋아요 수를 1 증가시킵니다.")
  public ResponseEntity<PostInfoDTO> likeCountIncrement(@PathVariable Long id) {
    PostInfoDTO updatedPost = postService.likeCountIncrement(id);
    return ResponseEntity.ok(updatedPost);
  }
}
