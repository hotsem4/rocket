package com.rocket.domains.posts.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.rocket.commons.exception.exceptions.AccessDeniedCustomException;
import com.rocket.commons.exception.exceptions.PostNotFoundException;
import com.rocket.domains.posts.application.assembler.PostResponseAssembler;
import com.rocket.domains.posts.application.dto.request.PostCreateRequest;
import com.rocket.domains.posts.application.dto.request.PostUpdateRequest;
import com.rocket.domains.posts.application.dto.response.PostDetailInfoResponse;
import com.rocket.domains.posts.application.dto.response.PostListResponse;
import com.rocket.domains.posts.domain.entity.Post;
import com.rocket.domains.posts.domain.repository.PostReader;
import com.rocket.domains.posts.domain.repository.PostWriter;
import com.rocket.domains.user.domain.entity.Address;
import com.rocket.domains.user.domain.entity.User;
import com.rocket.domains.user.domain.enums.Gender;
import com.rocket.domains.user.domain.enums.Role;
import com.rocket.domains.user.domain.facade.UserFacade;
import com.rocket.domains.user.domain.service.UserLookupService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

  @Mock
  private UserLookupService userLookupService;

  @Mock
  private PostReader postReader;

  @Mock
  private PostWriter postWriter;

  @Mock
  private PostResponseAssembler postResponseAssembler;

  @Mock
  private UserFacade userFacade;

  @InjectMocks
  private PostServiceImpl postService;

  private User normalUser;
  private User adminUser;
  private Post samplePost;

  @BeforeEach
  void setUp() {
    // Given
    normalUser = User.createWithIdForTest(
        1L, "test@example.com", "password123!", 25,
        Gender.MALE, new Address("state", "city", "street", "11111"),
        "testNick", "01099999999", Role.USER, null
    );
    adminUser = User.createWithIdForTest(
        2L, "admin@example.com", "adminPass!!!", 30,
        Gender.FEMALE, new Address("st", "ct", "st", "22222"),
        "adminNick", "01088888888", Role.ADMIN, null
    );

    samplePost = Post.create("SampleTitle", "SampleContent", normalUser);
    // 임의로 post.id = 100L 설정 (테스트용)
    // reflection or setter not shown in code, but you can do something like:
    // ReflectionTestUtils.setField(samplePost, "id", 100L);
  }

  // ------------------------------------------
  // HELPER METHODS
  // ------------------------------------------
  private User createUser(Long id, Role role) {
    User user = User.createWithIdForTest(
        id,                           // id
        "test@example.com",                  // email
        "password123!",                      // password
        25,                                  // age
        Gender.MALE,                         // gender
        new Address("state", "city", "street", "11111"), // address
        "testNick",                          // nickname
        "01099999999",                       // phoneNumber
        role,                            // role
        null
    );

    // user 필드에 직접 접근 or ReflectionTestUtils
    // 실제 코드에선 User.create(...) 팩토리 메서드 사용 가능
    // for simplicity here:
    // ReflectionTestUtils.setField(user, "id", id);
    // ReflectionTestUtils.setField(user, "role", role);

    // 시나리오 상 role과 id만 주입 (nickname, email 등은 생략)
    return user;
  }

  // ------------------------------------------
  // CREATE TESTS
  // ------------------------------------------
  @Test
  @DisplayName("정상 게시글 생성 - 존재하는 userId, valid dto")
  void savePost_success() {
    // Given
    Long validUserId = 1L;
    PostCreateRequest dto = new PostCreateRequest("title", "content");

    given(userLookupService.existsById(validUserId)).willReturn(true);
    given(userLookupService.findById(validUserId)).willReturn(Optional.of(normalUser));
    Post postEntity = PostMapper.toEntity(dto, normalUser);
    Post savedPost = Post.create("title", "content", normalUser);
    // mocking writer
    given(postWriter.savePost(any(Post.class))).willReturn(savedPost);
    // assembler mock
    given(postResponseAssembler.toDetailDto(savedPost)).willReturn(
        new PostDetailInfoResponse(100L, "title", "content", 1L, "nickname", null, 0)
    );

    // When
    PostDetailInfoResponse result = postService.savePost(dto, validUserId);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.title()).isEqualTo("title");
    then(userLookupService).should(times(1)).existsById(validUserId);
    then(userLookupService).should(times(1)).findById(validUserId);
    then(postWriter).should(times(1)).savePost(any(Post.class));
  }

  @Test
  @DisplayName("게시글 생성 실패 - userId 없음")
  void savePost_fail_noUserId() {
    // Given
    Long invalidUserId = 999L;
    PostCreateRequest dto = new PostCreateRequest("title", "content");
    given(userLookupService.existsById(invalidUserId)).willReturn(false);

    // When & Then
    assertThatThrownBy(() -> postService.savePost(dto, invalidUserId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("유효하지 않은 작성자 ID");

    then(userLookupService).should(times(1)).existsById(invalidUserId);
    then(userLookupService).should(never()).findById(anyLong());
  }

  // ------------------------------------------
  // UPDATE TESTS
  // ------------------------------------------
  @Test
  @DisplayName("정상 수정 - 작성자 본인")
  void updateById_success_owner() {
    // Given
    Long postId = 100L;
    Long userId = 1L; // owner
    PostUpdateRequest dto = new PostUpdateRequest("newTitle", "newContent");

    // Post
    given(postReader.findById(postId)).willReturn(Optional.of(samplePost));
    // User
    given(userFacade.findById(userId)).willReturn(Optional.of(normalUser));
    // Assembler
    given(postResponseAssembler.toDetailDto(samplePost)).willReturn(
        new PostDetailInfoResponse(postId, "newTitle", "newContent", userId, "nickname", null, 0)
    );

    // When
    PostDetailInfoResponse result = postService.updateById(postId, dto, userId);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.title()).isEqualTo("newTitle");
    then(postReader).should(times(1)).findById(postId);
    then(postResponseAssembler).should(times(1)).toDetailDto(samplePost);
  }

  @Test
  @DisplayName("정상 수정 - 관리자 (owner != admin but user.isAdmin())")
  void updateById_success_admin() {
    // Given
    Long postId = 100L;
    Long adminId = 2L; // admin
    PostUpdateRequest dto = new PostUpdateRequest("newTitle", "newContent");

    // Post owned by normalUser
    given(postReader.findById(postId)).willReturn(Optional.of(samplePost));
    // admin
    given(userFacade.findById(adminId)).willReturn(Optional.of(adminUser));
    // assembler
    given(postResponseAssembler.toDetailDto(samplePost))
        .willReturn(
            new PostDetailInfoResponse(postId, "newTitle", "newContent", 1L, "nickname", null, 0));

    // When
    PostDetailInfoResponse result = postService.updateById(postId, dto, adminId);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.title()).isEqualTo("newTitle");
  }

  @Test
  @DisplayName("게시글 수정 실패 - 게시글 없음")
  void updateById_fail_notFoundPost() {
    // Given
    Long postId = 999L;
    Long userId = 1L;
    PostUpdateRequest dto = new PostUpdateRequest("title", "content");
    given(postReader.findById(postId)).willReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> postService.updateById(postId, dto, userId))
        .isInstanceOf(PostNotFoundException.class);

    then(postReader).should(times(1)).findById(postId);
    then(userFacade).should(never()).findById(anyLong());
  }

  @Test
  @DisplayName("게시글 수정 실패 - 권한 없음 (작성자도 아니고, admin도 아님)")
  void updateById_fail_noAuthority() {
    // Given
    Long postId = 100L;
    Long randomUserId = 3L; // neither owner nor admin
    // samplePost.owner = normalUser
    given(postReader.findById(postId)).willReturn(Optional.of(samplePost));

    User randomUser = createUser(randomUserId, Role.USER); // not admin
    given(userFacade.findById(randomUserId)).willReturn(Optional.of(randomUser));

    // When & Then
    assertThatThrownBy(
        () -> postService.updateById(postId, new PostUpdateRequest("up", "go"), randomUserId))
        .isInstanceOf(AccessDeniedCustomException.class)
        .hasMessageContaining("게시글 수정 권한이 없습니다");

    then(postReader).should(times(1)).findById(postId);
    then(userFacade).should(times(1)).findById(randomUserId);
  }

  // ------------------------------------------
  // DELETE TESTS
  // ------------------------------------------
  @Test
  @DisplayName("정상 삭제 - 작성자 본인")
  void deleteById_success_owner() {
    // Given
    Long postId = 100L;
    Long ownerId = 1L;
    given(postReader.findById(postId)).willReturn(Optional.of(samplePost));
    given(userFacade.getByIdOrThrow(ownerId)).willReturn(normalUser);

    // When
    postService.deleteById(postId, ownerId);

    // Then
    then(postReader).should(times(1)).findById(postId);
    then(userFacade).should(times(1)).getByIdOrThrow(ownerId);
    then(postWriter).should(times(1)).deleteById(postId);
  }

  @Test
  @DisplayName("정상 삭제 - 관리자")
  void deleteById_success_admin() {
    // Given
    Long postId = 100L;
    Long adminId = 2L; // admin
    given(postReader.findById(postId)).willReturn(Optional.of(samplePost));
    given(userFacade.getByIdOrThrow(adminId)).willReturn(adminUser);

    // When
    postService.deleteById(postId, adminId);

    // Then
    then(postWriter).should(times(1)).deleteById(postId);
  }

  @Test
  @DisplayName("게시글 삭제 실패 - 게시글 없음")
  void deleteById_fail_notFoundPost() {
    // Given
    Long postId = 999L;
    given(postReader.findById(postId)).willReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> postService.deleteById(postId, 1L))
        .isInstanceOf(PostNotFoundException.class);

    then(postReader).should(times(1)).findById(postId);
    then(userFacade).should(never()).getByIdOrThrow(anyLong());
    then(postWriter).should(never()).deleteById(anyLong());
  }

  @Test
  @DisplayName("게시글 삭제 실패 - 권한 없음")
  void deleteById_fail_noAuthority() {
    // Given
    Long postId = 100L;
    Long otherUserId = 3L; // not owner or admin
    given(postReader.findById(postId)).willReturn(Optional.of(samplePost));
    User otherUser = createUser(otherUserId, Role.USER);
    given(userFacade.getByIdOrThrow(otherUserId)).willReturn(otherUser);

    // When & Then
    assertThatThrownBy(() -> postService.deleteById(postId, otherUserId))
        .isInstanceOf(AccessDeniedCustomException.class)
        .hasMessageContaining("게시글을 삭제할 권한이 없습니다");

    then(postReader).should(times(1)).findById(postId);
    then(userFacade).should(times(1)).getByIdOrThrow(otherUserId);
    then(postWriter).should(never()).deleteById(anyLong());
  }

  // ------------------------------------------
  // FIND TESTS
  // ------------------------------------------
  @Test
  @DisplayName("게시글 상세 조회 - 성공")
  void findById_success() {
    // Given
    Long postId = 100L;
    given(postReader.findById(postId)).willReturn(Optional.of(samplePost));
    // assembler
    PostDetailInfoResponse mockResponse = new PostDetailInfoResponse(
        postId, "SampleTitle", "SampleContent", 1L, "nickname", null, 0
    );
    given(postResponseAssembler.toDetailDto(samplePost)).willReturn(mockResponse);

    // When
    PostDetailInfoResponse result = postService.findById(postId);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.title()).isEqualTo("SampleTitle");
  }

  @Test
  @DisplayName("게시글 상세 조회 - 게시글 없음")
  void findById_fail_notFound() {
    // Given
    Long postId = 999L;
    given(postReader.findById(postId)).willReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> postService.findById(postId))
        .isInstanceOf(PostNotFoundException.class);
  }

  @Test
  @DisplayName("게시글 전체 목록 조회 - 성공")
  void findAllPosts_success() {
    // Given
    Post p1 = Post.create("t1", "c1", normalUser);
    Post p2 = Post.create("t2", "c2", normalUser);
    given(postReader.findAll()).willReturn(List.of(p1, p2));
    // map to DTO
    PostListResponse resp1 = new PostListResponse(101L, p1.getTitle(), "nick1", null);
    PostListResponse resp2 = new PostListResponse(102L, p2.getTitle(), "nick2", null);

    // When
    List<PostListResponse> result = postService.findAllPosts();

    assertThat(result).hasSize(2);
  }

  @Test
  @DisplayName("제목으로 게시글 검색 - 정상 케이스")
  void findByTitle_success() {
    // Given
    String title = "someTitle";
    Post p1 = Post.create("someTitle", "content1", normalUser);
    Post p2 = Post.create("someTitle", "content2", normalUser);

    given(postReader.findByTitle(title)).willReturn(List.of(p1, p2));

    // When
    List<PostListResponse> result = postService.findByTitle(title);

    // Then
    // postService.findByTitle() 내부에서 PostMapper.toListDto(...) 한다고 가정
    // 여기서는 결과 사이즈만 검증
    assertThat(result).hasSize(2);
  }
}

