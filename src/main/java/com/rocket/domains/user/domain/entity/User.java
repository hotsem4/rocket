package com.rocket.domains.user.domain.entity;

import com.rocket.domains.user.application.dto.request.AddressRequest;
import com.rocket.domains.user.application.dto.request.UserUpdateRequest;
import com.rocket.domains.user.domain.enums.Gender;
import com.rocket.domains.user.domain.enums.Role;
import com.rocket.domains.user.domain.enums.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "이메일은 필수 입력값입니다.")
  @Email(message = "올바른 이메일 형식이 아닙니다.")
  @Column(name = "email", unique = true, nullable = false)
  @Comment("이메일")
  private String email;

  @NotBlank(message = "비밀번호는 필수 입력값입니다.")
  @Size(min = 10, message = "비밀번호는 최소 10자 이상이어야 합니다.")
  @Column(name = "password", nullable = false)
  @Comment("비밀번호")
  private String password;

  @NotBlank(message = "닉네임은 필수 입력값입니다.")
  @Size(max = 30, message = "닉네임은 최대 30자까지 입력 가능합니다.")
  @Column(name = "nickname", nullable = false, unique = true)
  @Comment("닉네임")
  private String nickname;


  @NotNull(message = "나이는 필수 입력값입니다.")
  @Min(value = 0, message = "나이는 음수일 수 없습니다.")
  @Column(name = "age", nullable = false)
  @Comment("나이")
  private int age;

  @NotNull(message = "성별은 필수 입력값입니다.")
  @Column(name = "gender", nullable = false)
  @Comment("성별")
  @Enumerated(EnumType.STRING)
  private Gender gender;

  @Valid
  @NotNull(message = "주소는 필수 입력값입니다.")
  @Embedded
  private Address address;

  @NotNull(message = "전화번호는 필수 입력값입니다.")
  @NotBlank
  @Column(name = "phone_number", nullable = false)
  private String phoneNumber;


  @Enumerated(EnumType.STRING)
  @Comment("역할")
  private Role role;

  @Column(name = "profile_url")
  @Comment("프로필 이미지 URL")
  private String profileUrl;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Comment("계정 상태")
  private UserStatus status;

  @Column(name = "manner_score", nullable = false)
  @Comment("매너 점수")
  private Integer mannerScore;

  @Column(name = "joined_group_id", nullable = true)
  @Comment("가입한 그룹의 아이디")
  private Long joinedGroupId;

  @Column(name = "created_group_id")
  @Comment("본인이 만든 그룹 아이디")
  private Long createdGroupId;

  @CreatedDate
  @Column(name = "created_at", updatable = false)
  @Comment("User 생성 일자")
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Comment("User 업데이트 일자")
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;


  private User(String email, String password, int age, Gender gender, Address address,
      String nickname, String phoneNumber, Role role, String profileUrl, UserStatus status,
      Integer mannerScore, Long createdGroupId, Long joinedGroupId) {
    this.email = email;
    this.password = password;
    this.age = age;
    this.gender = gender;
    this.address = address;
    this.nickname = nickname;
    this.phoneNumber = phoneNumber;
    this.role = role;
    this.profileUrl = profileUrl;
    this.status = status;
    this.mannerScore = mannerScore;
    this.createdGroupId = createdGroupId;
    this.joinedGroupId = joinedGroupId;
  }

  // @VisibleForTesting
  public static User createWithIdForTest(
      Long id, String email, String password, int age, Gender gender, Address address,
      String nickname, String phoneNumber, Role role, String profileUrl, UserStatus status,
      Integer mannerScore, Long createdGroupId, Long joinedGroupId
  ) {
    User user = new User(email, password, age, gender, address, nickname, phoneNumber, role,
        profileUrl, status, mannerScore, createdGroupId, joinedGroupId);
    user.id = id;
    return user;
  }


  @Builder
  public static User create(
      @NotBlank String email,
      @NotBlank String password,
      @Min(0) int age,
      @NotNull Gender gender,
      @Valid @NotNull Address address,
      @NotBlank String nickname,
      @NotBlank String phoneNumber,
      @NotBlank Role role,
      String profileUrl
  ) {
    return new User(
        email,
        password,
        age,
        gender,
        address,
        nickname,
        phoneNumber,
        role,
        profileUrl,
        UserStatus.ACTIVE,
        60,
        null,
        null
    );
  }


  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return age == user.age && Objects.equals(id, user.id) && Objects.equals(email,
        user.email) && Objects.equals(password, user.password) && Objects.equals(
        nickname, user.nickname) && gender == user.gender && Objects.equals(address,
        user.address) && Objects.equals(phoneNumber, user.phoneNumber) && role == user.role
        && Objects.equals(profileUrl, user.profileUrl) && status == user.status
        && Objects.equals(mannerScore, user.mannerScore) && Objects.equals(
        joinedGroupId, user.joinedGroupId) && Objects.equals(createdGroupId,
        user.createdGroupId) && Objects.equals(createdAt, user.createdAt)
        && Objects.equals(updatedAt, user.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, email, password, nickname, age, gender, address, phoneNumber, role,
        profileUrl, status, mannerScore, joinedGroupId, createdGroupId, createdAt, updatedAt);
  }

  public void updateFrom(UserUpdateRequest dto) {
    updateEmailIfChanged(dto.email());
    updateAgeIfChanged(dto.age());
    updateGenderIfChanged(dto.gender());
    updateNicknameIfChanged(dto.nickname());
    updatePhoneNumberIfChanged(dto.phoneNumber());
    updateProfileUrlIfChanged(dto.profileUrl());
    updateAddressIfChanged(dto.address());
  }

  private void updateEmailIfChanged(String email) {
    if (email != null && !this.email.equals(email)) {
      this.email = email;
    }
  }

  private void updateAgeIfChanged(Integer age) {
    if (age != null && age >= 0 && this.age != age) {
      this.age = age;
    }
  }

  private void updateGenderIfChanged(Gender gender) {
    if (gender != null && this.gender != gender) {
      this.gender = gender;
    }
  }

  private void updateNicknameIfChanged(String nickname) {
    if (nickname != null && !this.nickname.equals(nickname)) {
      this.nickname = nickname;
    }
  }

  private void updatePhoneNumberIfChanged(String phoneNumber) {
    if (phoneNumber != null && !this.phoneNumber.equals(phoneNumber)) {
      this.phoneNumber = phoneNumber;
    }
  }

  private void updateProfileUrlIfChanged(String profileUrl) {
    if (profileUrl != null && !profileUrl.equals(this.profileUrl)) {
      this.profileUrl = profileUrl;
    }
  }

  private void updateAddressIfChanged(AddressRequest dto) {
    if (dto == null) return;

    Address newAddress = new Address(dto.state(), dto.city(), dto.street(), dto.zipCode());
    if (!this.address.equals(newAddress)) {
      this.address = newAddress;
    }
  }


  public void updatePassword(String newPassword) {
    if (newPassword == null || newPassword.length() < 6) {
      throw new IllegalArgumentException("비밀번호 형식이 올바르지 않습니다.");
    }
    this.password = newPassword;
  }

  public void activate() {
    if (this.status == UserStatus.ACTIVE) {
      throw new IllegalStateException("이미 활성화된 유저입니다.");
    }
    this.status = UserStatus.ACTIVE;
  }

  public void suspend() {
    if (this.status == UserStatus.BANNED) {
      throw new IllegalStateException("영구 정지된 유저는 상태 변경이 불가합니다.");
    }
    if (this.status == UserStatus.SUSPENDED) {
      return;
    }
    this.status = UserStatus.SUSPENDED;
  }

  public void ban() {
    if (this.status == UserStatus.BANNED) {
      throw new IllegalStateException("이미 영구 정지된 유저여서 상태 변경이 불가능합니다.");
    }
    this.status = UserStatus.BANNED;
  }
}
