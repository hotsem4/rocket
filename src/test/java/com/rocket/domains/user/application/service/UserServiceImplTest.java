package com.rocket.domains.user.application.service;

import static com.rocket.domains.user.domain.entity.User.createWithIdForTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rocket.commons.exception.exceptions.UserNotFoundException;
import com.rocket.domains.user.application.dto.request.AddressRequest;
import com.rocket.domains.user.application.dto.request.UserRegisterRequest;
import com.rocket.domains.user.application.dto.request.UserUpdateRequest;
import com.rocket.domains.user.application.dto.response.UserInfoResponse;
import com.rocket.domains.user.domain.entity.Address;
import com.rocket.domains.user.domain.entity.User;
import com.rocket.domains.user.domain.enums.Gender;
import com.rocket.domains.user.domain.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserServiceImpl userService;

  private User user;
  private UserRegisterRequest userRegisterRequest;

  private UserUpdateRequest userUpdateRequest;
  private UserInfoResponse userInfoResponse;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    Address address = new Address("State", "City", "Street", "Zip");
    // 테스트 전용 정적 메서드를 사용하여 ID를 포함한 User 객체 생성
    user = createWithIdForTest(1L, "test@example.com", "password", 30, Gender.MALE, address,
        "Toin");

    // 비밀번호 암호화 스텁: 모든 입력에 대해 "encodedPassword"를 반환합니다.
    when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedPassword");

    // 회원가입 DTO 생성
    userRegisterRequest = new UserRegisterRequest(
        "test@example.com",
        "password",
        "30",
        Gender.MALE,
        new AddressRequest("State", "City", "Street", "Zip"),
        "Toni"
    );

    // 사용자 수정 DTO: 업데이트할 내용
    userUpdateRequest = new UserUpdateRequest(
        "test@example.com",
        35,
        Gender.FEMALE,
        new AddressRequest("State", "City", "NewStreet", "Zip"),
        "Toni"
    );

    // 응답용 UserDTO 생성 (User -> DTO 변환)
    userInfoResponse = UserInfoResponse.fromUser(user);
  }

  @DisplayName("User 저장 - 성공")
  @Test
  void saveUser_success() {
    // Given: Repository가 정상적으로 저장된 User를 반환한다고 가정
    when(userRepository.saveUser(any(User.class))).thenReturn(user);

    // When
    User result = userService.saveUser(userRegisterRequest);

    // Then: 반환된 User 객체가 null이 아니고 이메일이 일치하는지 검증
    assertThat(result).isNotNull();
    assertThat(result.getEmail()).isEqualTo("test@example.com");
    verify(userRepository, times(1)).saveUser(any(User.class));
  }

  @DisplayName("User 조회 by email - 성공")
  @Test
  void findByEmail_success() {
    // Given
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

    // When
    UserInfoResponse result = userService.findByEmail("test@example.com");

    // Then
    assertThat(result).isNotNull();
    assertThat(result.email()).isEqualTo("test@example.com");
    verify(userRepository, times(1)).findByEmail(anyString());
  }

  @DisplayName("User 조회 by email - 실패 (사용자 없음)")
  @Test
  void findByEmail_fail_notFound() {
    // Given
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> userService.findByEmail("nonexistent@example.com"))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessageContaining("nonexistent@example.com");
  }

  @DisplayName("전체 User 조회 - 성공")
  @Test
  void findAllUsers_success() {
    // Given
    when(userRepository.findAll()).thenReturn(List.of(user));

    // When
    List<UserInfoResponse> result = userService.findAllUsers();

    // Then
    assertThat(result).isNotEmpty();
    assertThat(result).hasSize(1);
    verify(userRepository, times(1)).findAll();
  }

  @DisplayName("User 수정 by email - 성공")
  @Test
  void updateByEmail_success() {
    // Given: 존재하는 사용자, 업데이트 후 조회 시 수정된 User 객체 반환
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(user));

    Address updatedAddress = new Address("State", "City", "NewStreet", "Zip");
    User updatedUser = createWithIdForTest(1L, "test@example.com", "password", 35, Gender.FEMALE,
        updatedAddress, "Toni");
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(updatedUser));

    // When
    UserInfoResponse result = userService.updateByEmail(userUpdateRequest);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.age()).isEqualTo(35);
    assertThat(result.gender()).isEqualTo(Gender.FEMALE);
    // 각 업데이트 메서드 호출 여부는 Repository 구현에 따라 verify 추가 가능
  }

  @DisplayName("User 수정 by email - 실패 (수정할 내용 없음)")
  @Test
  void updateByEmail_fail_noUpdateFields() {
    // Given: 업데이트할 필드가 모두 null인 경우
    UserUpdateRequest updateDTO = new UserUpdateRequest("test@example.com", null, null, null, null);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(user));

    // When & Then: 변경할 내용이 없으면 예외 발생
    assertThatThrownBy(() -> userService.updateByEmail(updateDTO))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("변경된 내용이 없습니다.");
  }

  @DisplayName("User 수정 by email - 실패 (사용자 없음)")
  @Test
  void updateByEmail_fail_userNotFound() {
    // Given: 존재하지 않는 사용자
    when(userRepository.existsByEmail(anyString())).thenReturn(false);

    // When & Then
    assertThatThrownBy(() -> userService.updateByEmail(userUpdateRequest))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessageContaining("이메일이 test@example.com인 유저 정보를 찾을 수 없습니다.");
  }

  @DisplayName("User 삭제 by email - 성공")
  @Test
  void deleteByEmail_success() {
    // Given
    when(userRepository.existsByEmail(anyString())).thenReturn(true);
    when(userRepository.deleteUser(anyString())).thenReturn(true);

    // When
    boolean result = userService.deleteByEmail("test@example.com");

    // Then
    assertThat(result).isTrue();
    verify(userRepository, times(1)).existsByEmail(anyString());
    verify(userRepository, times(1)).deleteUser(anyString());
  }

  @DisplayName("User 삭제 by email - 실패 (사용자 없음)")
  @Test
  void deleteByEmail_fail_notFound() {
    // Given
    when(userRepository.existsByEmail(anyString())).thenReturn(false);

    // When & Then
    assertThatThrownBy(() -> userService.deleteByEmail("nonexistent@example.com"))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessageContaining("nonexistent@example.com");
  }
}
