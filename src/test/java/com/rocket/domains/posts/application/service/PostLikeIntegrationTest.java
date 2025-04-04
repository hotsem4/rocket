package com.rocket.domains.posts.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.rocket.commons.exception.exceptions.DuplicateLikeException;
import com.rocket.domains.posts.domain.entity.Post;
import com.rocket.domains.posts.domain.entity.PostLike;
import com.rocket.domains.posts.domain.repository.PostLikeCacheRepository;
import com.rocket.domains.posts.domain.repository.PostReader;
import com.rocket.domains.posts.domain.repository.PostWriter;
import com.rocket.domains.posts.domain.service.PostService;
import com.rocket.domains.posts.infrastructure.persistence.jpa.PostLikeRepository;
import com.rocket.domains.user.application.dto.response.UserSimpleInfoResponse;
import com.rocket.domains.user.domain.entity.Address;
import com.rocket.domains.user.domain.entity.User;
import com.rocket.domains.user.domain.enums.Gender;
import com.rocket.domains.user.domain.enums.Role;
import com.rocket.domains.user.domain.facade.UserFacade;
import com.rocket.domains.user.domain.repository.UserWriter;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class PostLikeIntegrationTest {

  @Autowired
  private PostLikeService postLikeService;
  @Autowired
  private PostService postService;
  @Autowired
  private UserFacade userFacade;
  @Autowired
  private PostWriter postWriter;
  @Autowired
  private PostReader postReader;
  @Autowired
  private PostLikeCacheRepository redisRepo;
  @Autowired
  private PostLikeRepository postLikeRepository;
  @Autowired
  private UserWriter userWriter;

  private User user;
  private Post post;

  @BeforeEach
  void setup() {
    user = User.create(
        "test@rocket.com",
        "password1234",
        25,
        Gender.MALE,
        new Address("서울", "강남구", "테헤란로", "12345"),
        "테스터",
        "01012345678",
        Role.USER,
        null);
    userWriter.saveUser(user);
    post = Post.create("테스트 제목", "테스트 내용", user);
    postWriter.savePost(post);
  }

  @Test
  void 좋아요_성공시_Redis값_증가() {
    postLikeService.likePost(post.getId(), user.getId());

    int redisCount = redisRepo.getLikeCount(post.getId());
    assertThat(redisCount).isEqualTo(1);
  }

  @Test
  void 중복_좋아요시_예외_발생() {
    postLikeService.likePost(post.getId(), user.getId());

    assertThrows(DuplicateLikeException.class,
        () -> postLikeService.likePost(post.getId(), user.getId()));
  }

  @Test
  void Redis_값만_있고_DB에_반영되지_않은_상태_확인() {
    postLikeService.likePost(post.getId(), user.getId());

    Post updated = postReader.findById(post.getId()).get();
    assertThat(updated.getLikeCount()).isEqualTo(0); // 아직 동기화 안 됨
    assertThat(redisRepo.getLikeCount(post.getId())).isEqualTo(1);
  }

  @Test
  void Redis_값이_DB로_동기화되는지_확인() {
    postLikeService.likePost(post.getId(), user.getId());

    postLikeService.syncRedisLikeToDB(); // 강제 동기화

    Post updated = postReader.findById(post.getId()).get();
    assertThat(updated.getLikeCount()).isEqualTo(1);
  }

  @Test
  void Redis에_값이_있을_때_좋아요_취소시_Redis감소_DB유지() {
    postLikeService.likePost(post.getId(), user.getId());
    postLikeService.unlikePost(post.getId(), user.getId());

    assertThat(redisRepo.getLikeCount(post.getId())).isEqualTo(0);
    assertThat(postLikeRepository.findByPostIdAndUserId(post.getId(), user.getId())).isEmpty();
  }

  @Test
  void Redis에_값이_없을때_좋아요_취소시_DB에서만_감소() {
    postLikeService.likePost(post.getId(), user.getId());
    postLikeService.syncRedisLikeToDB(); // 동기화
    postLikeService.unlikePost(post.getId(), user.getId());

    Post updated = postReader.findById(post.getId()).get();
    assertThat(updated.getLikeCount()).isEqualTo(0);
  }

  @Test
  void 좋아요_누른_유저_목록_정상조회() {
    User other = User.create(
        "other@rocket.com", "pass123423es", 28, Gender.FEMALE,
        new Address("부산", "해운대", "센텀대로", "54321"),
        "다른유저", "01099998888", Role.USER, null);

    userWriter.saveUser(other);

    postLikeService.likePost(post.getId(), user.getId());
    postLikeService.likePost(post.getId(), other.getId());

    List<Long> userIds = postLikeRepository.findAllByPostId(post.getId())
        .stream().map(PostLike::getUserId).toList();

    List<UserSimpleInfoResponse> result = userFacade.findUserInfoList(userIds);

    assertThat(result).hasSize(2);
    assertThat(result).extracting("nickname")
        .containsExactlyInAnyOrder("테스터", "다른유저");
  }
}
