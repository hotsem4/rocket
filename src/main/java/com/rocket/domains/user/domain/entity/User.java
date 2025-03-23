package com.rocket.domains.user.domain.entity;

import com.rocket.domains.posts.domain.entity.Post;
import com.rocket.domains.user.application.dto.request.AddressRequest;
import com.rocket.domains.user.domain.enums.Gender;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "User")
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
  @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
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
  @Comment("나이")
  @Enumerated(EnumType.STRING)
  private Gender gender;

  @Valid
  @NotNull(message = "주소는 필수 입력값입니다.")
  @Embedded
  private Address address;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Post> posts = new ArrayList<>();

  private User(String email, String password, int age, Gender gender, Address address,
      String nickname) {
    this.email = email;
    this.password = password;
    this.age = age;
    this.gender = gender;
    this.address = address;
    this.nickname = nickname;
  }

  // @VisibleForTesting
  public static User createWithIdForTest(
      Long id, String email, String password, int age, Gender gender, Address address,
      String nickname
  ) {
    User user = new User(email, password, age, gender, address, nickname);
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
      @NotBlank String nickname
  ) {
    return new User(email, password, age, gender, address, nickname);
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
        user.address);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, email, password, nickname, age, gender, address);
  }

  public void updateNickname(String newNickname) {
    if (newNickname == null) {
      throw new IllegalArgumentException("닉네임은 null일 수 없습니다.");
    }
    this.nickname = newNickname;
  }

  public void updateAge(Integer age) {
    if (age < 0) {
      throw new IllegalArgumentException("나이는 음수일 수 없습니다.");
    }
    this.age = age;
  }

  public void updateGender(Gender gender) {
    if (gender == null) {
      throw new IllegalArgumentException("성별은 null일 수 없습니다.");
    }
    this.gender = gender;
  }

  public void updateAddress(AddressRequest address) {
    if (address == null) {
      throw new IllegalArgumentException("주소는 null일 수 없습니다.");
    }
    this.address = new Address(
        address.state(),
        address.city(),
        address.street(),
        address.zipCode()
    );
  }
}
