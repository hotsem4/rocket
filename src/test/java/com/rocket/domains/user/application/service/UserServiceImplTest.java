package com.rocket.domains.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.rocket.commons.exception.exceptions.UserNotFoundException;
import com.rocket.domains.user.application.dto.common.AddressDTO;
import com.rocket.domains.user.application.dto.request.UserRegisterDTO;
import com.rocket.domains.user.application.dto.request.UserUpdateDTO;
import com.rocket.domains.user.application.dto.response.UserDTO;
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


class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserServiceImpl userService;

  private User user;
  private UserRegisterDTO userRegisterDTO;
  private UserUpdateDTO userUpdateDTO;
  private UserDTO userDTO;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    Address address = new Address("State", "City", "Street", "Zip");
    user = new User(1L, "test@example.com", "password", 30, Gender.MALE, address);

    // UserRegisterDTO 생성 (생성 시 toEntity() 내부에서 Address 객체로 변환한다고 가정)
    userRegisterDTO = new UserRegisterDTO(
        "test@example.com",
        "password",
        "30",
        Gender.MALE,
        new AddressDTO("State", "City", "Street", "Zip")
    );

    // UserUpdateDTO: 업데이트할 내용이 있는 경우
    userUpdateDTO = new UserUpdateDTO(
        "test@example.com",
        35,
        Gender.FEMALE,
        new AddressDTO("State", "City", "NewStreet", "Zip")
    );

    // UserDTO (응답용)
    userDTO = UserDTO.from(user);
  }

  @DisplayName("User 저장 - 성공")
  @Test
  void saveUser_success() {
    // Given: Repository saveUser()가 정상적으로 저장된 User를 반환한다고 가정
    when(userRepository.saveUser(any(User.class))).thenReturn(true);

    // When
    Boolean result = userService.saveUser(userRegisterDTO);

    // Then
    assertThat(result).isTrue();
    verify(userRepository, times(1)).saveUser(any(User.class));
  }

  @DisplayName("User 조회 by email - 성공")
  @Test
  void findByEmail_success() {
    // Given
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

    // When
    UserDTO result = userService.findByEmail("test@example.com");

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
    List<UserDTO> result = userService.findAllUsers();

    // Then
    assertThat(result).isNotEmpty();
    assertThat(result).hasSize(1);
    verify(userRepository, times(1)).findAll();
  }

  @DisplayName("User 수정 by email - 성공")
  @Test
  void updateByEmail_success() {
    // Given: 존재하는 사용자, 업데이트 호출 후 findByEmail에서 수정된 사용자 반환
    when(userRepository.existsByEmail(anyString())).thenReturn(true);

    // 업데이트 작업은 void이므로 stub 처리
    // 수정 후 조회 시 수정된 내용을 포함한 User 객체 반환
    Address updatedAddress = new Address("State", "City", "NewStreet", "Zip");
    User updatedUser = new User(1L, "test@example.com", "password", 35, Gender.FEMALE, updatedAddress);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(updatedUser));

    // When
    UserDTO result = userService.updateByEmail(userUpdateDTO);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.age()).isEqualTo(35);
    assertThat(result.gender()).isEqualTo(Gender.FEMALE);
    verify(userRepository, times(1)).existsByEmail(anyString());
    // 각 update 메서드 호출 여부는 Repository 구현에 따라 다르지만 verify 호출 가능
  }

  @DisplayName("User 수정 by email - 실패 (수정할 내용 없음)")
  @Test
  void updateByEmail_fail_noUpdateFields() {
    // Given: 업데이트할 필드가 모두 null인 경우
    UserUpdateDTO updateDTO = new UserUpdateDTO("test@example.com", null, null, null);
    when(userRepository.existsByEmail(anyString())).thenReturn(true);

    // When & Then: Service에서 "변경할 내용이 없습니다." 예외 발생해야 함
    assertThatThrownBy(() -> userService.updateByEmail(updateDTO))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("변경한 내용이 없습니다.");
  }

  @DisplayName("User 수정 by email - 실패 (사용자 없음)")
  @Test
  void updateByEmail_fail_userNotFound() {
    // Given: 존재하지 않는 사용자
    when(userRepository.existsByEmail(anyString())).thenReturn(false);

    // When & Then
    assertThatThrownBy(() -> userService.updateByEmail(userUpdateDTO))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("해당 이메일의 사용자가 존재하지 않습니다.");
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
