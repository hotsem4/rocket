package com.rocket.domains.posts.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rocket.commons.exception.exceptions.PostNotFoundException;
import com.rocket.domains.posts.application.dto.common.PostDTO;
import com.rocket.domains.posts.application.dto.request.PostUpdateDTO;
import com.rocket.domains.posts.application.dto.response.PostInfoDTO;
import com.rocket.domains.posts.domain.entity.Post;
import com.rocket.domains.posts.domain.enums.repository.PostRepository;
import com.rocket.domains.user.domain.service.UserLookupService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PostServiceImplTest {
  @Mock
  private PostRepository postRepository;
  @Mock
  private UserLookupService userLookupService;

  @InjectMocks
  private PostServiceImpl postService;

  private Post post;
  private PostDTO postDTO;


  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    post = new Post(1L, "Test Title", "Test Content", 1L, LocalDateTime.now());
    postDTO = new PostDTO("Test Title", "Test Content", 1L, 0);
  }

  @DisplayName("게시글 저장 - 성공")
  @Test
  void savePost_success() {
    // Given
    when(userLookupService.existsById(anyLong())).thenReturn(true); // 작성자 ID 유효하다고 가정
    when(postRepository.savePost(any(Post.class))).thenReturn(post);

    // When
    PostInfoDTO result = postService.savePost(postDTO);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.title()).isEqualTo(post.getTitle());
    assertThat(result.content()).isEqualTo(post.getContent());
    verify(postRepository, times(1)).savePost(any(Post.class));
  }

  @DisplayName("게시글 저장 - 실패 (빈 제목)")
  @Test
  void savePost_fail_emptyTitle() {
    when(userLookupService.existsById(anyLong())).thenReturn(true); // 작성자 ID 유효하다고 가정
    PostDTO invalidDto = new PostDTO("", "Valid Content", 1L, 0);

    assertThatThrownBy(() -> postService.savePost(invalidDto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("제목은 필수 입력값이며, 비어 있을 수 없습니다.");
  }

  @DisplayName("게시글 저장 - 실패 (빈 내용)")
  @Test
  void savePost_fail_emptyContent() {
    when(userLookupService.existsById(anyLong())).thenReturn(true); // 작성자 ID 유효하다고 가정

    PostDTO invalidDto = new PostDTO("Valid Title", "", 1L, 0);

    assertThatThrownBy(() -> postService.savePost(invalidDto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("내용은 필수 입력값이며, 비어 있을 수 없습니다.");
  }

  @DisplayName("제목으로 게시글 찾기 - 성공")
  @Test
  void findByTitle_success() {
    // Given
    when(postRepository.findByTitle(anyString())).thenReturn(List.of(post));

    // When
    List<PostDTO> result = postService.findByTitle("Test");



    // Then
    assertThat(result).isNotEmpty();
    assertThat(result.getFirst().title()).isEqualTo(post.getTitle());
    verify(postRepository, times(1)).findByTitle(anyString());
  }

  @DisplayName("제목으로 게시글 찾기 - 실패 (검색 결과 없음)")
  @Test
  void findByTitle_fail_noResult() {
    when(postRepository.findByTitle(anyString())).thenReturn(Collections.emptyList());

    List<PostDTO> result = postService.findByTitle("Nonexistent");

    assertThat(result).isEmpty();
  }




  @DisplayName("전체 게시글 조회 - 성공")
  @Test
  void findAllPosts_success() {
    // Given
    when(postRepository.findAll()).thenReturn(List.of(post));

    // When
    List<PostDTO> result = postService.findAllPosts();

    // Then
    assertThat(result).isNotEmpty();
    assertThat(result.size()).isEqualTo(1);
    verify(postRepository, times(1)).findAll();
  }


  @DisplayName("ID로 게시글 찾기 - 성공")
  @Test
  void findById_success() {
    // Given
    when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

    // When
    PostInfoDTO result = postService.findById(1L);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.title()).isEqualTo(post.getTitle());
    verify(postRepository, times(1)).findById(anyLong());
  }

  @DisplayName("ID로 게시글 찾기 - 실패 (ID가 null)")
  @Test
  void findById_fail_nullId() {
    assertThatThrownBy(() -> postService.findById(null))
        .isInstanceOf(PostNotFoundException.class)
        .hasMessageContaining("null인 id를 갖고 있는 post를 찾을 수 없습니다.");
  }

  @DisplayName("ID로 게시글 찾기 - 실패 (게시글 없음)")
  @Test
  void findById_fail_postNotFound() {
    // Given
    when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> postService.findById(1L))
        .isInstanceOf(PostNotFoundException.class)
        .hasMessageContaining("1");
  }

  @DisplayName("게시글 수정 - 성공")
  @Test
  void updateById_success() {
    // Given
    PostUpdateDTO updateDTO = new PostUpdateDTO("Updated Title", "Updated Content");
    when(postRepository.updateById(anyLong(), anyString(), anyString())).thenReturn(true);
    when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

    // When
    PostInfoDTO result = postService.updateById(1L, updateDTO);

    // Then
    assertThat(result).isNotNull();
    verify(postRepository, times(1)).updateById(anyLong(), anyString(), anyString());
    verify(postRepository, times(1)).findById(anyLong());
  }

  @DisplayName("게시글 수정 - 실패 (수정할 내용 없음)")
  @Test
  void updateById_fail_noUpdateFields() {
    PostUpdateDTO updateDTO = new PostUpdateDTO(null, null);

    assertThatThrownBy(() -> postService.updateById(1L, updateDTO))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("변경할 값이 없습니다.");
  }

  @DisplayName("게시글 삭제 - 성공")
  @Test
  void deleteById_success() {
    // Given
    when(postRepository.deleteById(anyLong())).thenReturn(true);

    // When
    boolean result = postService.deleteById(1L);

    // Then
    assertThat(result).isTrue();
    verify(postRepository, times(1)).deleteById(anyLong());
  }

  @DisplayName("게시글 삭제 - 실패 (게시글 없음)")
  @Test
  void deleteById_fail_notFound() {
    when(postRepository.deleteById(anyLong())).thenReturn(false);

    assertThatThrownBy(() -> postService.deleteById(1L))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("해당 ID의 게시글을 찾을 수 없습니다.");
  }

  @DisplayName("게시글 좋아요 증가 - 성공")
  @Test
  void likeCountIncrement_success() {
    // Given
    // Repository의 incrementLikeCount()가 성공적으로 업데이트된다고 가정합니다.
    when(postRepository.incrementLikeCount(anyLong())).thenReturn(true);

    // findById()가 업데이트된 게시글을 반환하도록 stub 합니다.
    // 업데이트된 게시글은 좋아요 수가 1 증가한 상태로 가정합니다.
    Post updatedPost = new Post(1L, "Test Title", "Test Content", 1L, LocalDateTime.now()) {
      @Override
      public int getLikeCount() {
        return 1; // 좋아요 수가 1로 증가한 상태
      }
    };
    when(postRepository.findById(anyLong())).thenReturn(Optional.of(updatedPost));

    // When
    PostInfoDTO result = postService.likeCountIncrement(1L);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.likeCount()).isEqualTo(1);
    verify(postRepository, times(1)).incrementLikeCount(anyLong());
    verify(postRepository, times(1)).findById(anyLong());
  }

}