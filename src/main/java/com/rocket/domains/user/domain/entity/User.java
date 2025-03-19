package com.rocket.domains.user.domain.entity;

import com.rocket.domains.user.domain.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
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
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name="User")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
  @Column(name="password", nullable = false)
  @Comment("비밀번호")
  private String password;

  @NotNull(message = "나이는 필수 입력값입니다.")
  @Min(value = 0, message = "나이는 음수일 수 없습니다.")
  @Column(name="age", nullable = false)
  @Comment("나이")
  private int age;

  @NotNull(message = "성별은 필수 입력값입니다.")
  @Column(name="gender", nullable = false)
  @Comment("나이")
  @Enumerated(EnumType.STRING)
  private Gender gender;

  @Valid
  @NotNull(message = "주소는 필수 입력값입니다.")
  @Embedded
  private Address address;

  private User(String email, String password, int age, Gender gender, Address address) {
    this.email = email;
    this.password = password;
    this.age = age;
    this.gender = gender;
    this.address = address;
  }

  @Builder
  public static User create(
      @NotBlank String email,
      @NotBlank String password,
      @Min(0) int age,
      @NotNull Gender gender,
      @Valid @NotNull Address address
  ) {
    return new User(email, password, age, gender, address);
  }

  public Long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public int getAge() {
    return age;
  }

  public Gender getGender() {
    return gender;
  }

  public Address getAddress() {
    return address;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return age == user.age && Objects.equals(id, user.id) &&
        Objects.equals(email, user.email) &&
        Objects.equals(password, user.password) &&
        gender == user.gender && Objects.equals(address, user.address);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, email, password, age, gender, address);
  }
}
