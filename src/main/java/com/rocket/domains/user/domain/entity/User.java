package com.rocket.domains.user.domain.entity;

import com.rocket.domains.user.domain.enums.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Objects;

public class User {
  private Long id;

  @NotBlank(message = "이메일은 필수 입력값입니다.")
  @Email(message = "올바른 이메일 형식이 아닙니다.")
  private final String email;

  @NotBlank(message = "비밀번호는 필수 입력값입니다.")
  @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
  private final String password;

  @Min(value = 0, message = "나이는 음수일 수 없습니다.")
  private final int age;

  @NotNull(message = "성별은 필수 입력값입니다.")
  private final Gender gender;

  @Valid
  @NotNull(message = "주소는 필수 입력값입니다.")
  private final Address address;

  // 조회 시 사용할 생성자 추가
  public User(Long id, String email, String password, int age, Gender gender, Address address) {
    this.id = id;
    this.email = email;
    this.password = password;
    this.age = age;
    this.gender = gender;
    this.address = address;
  }

  // 빌더 패턴 적용
  public User(Builder builder) {
    this.email = builder.email;
    this.password = builder.password;
    this.age = builder.age;
    this.gender = builder.gender;
    this.address = builder.address;
  }

  public Long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
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

  // 빌더 패턴 추가
  public static class Builder {
    private String email;
    private String password;
    private int age;
    private Gender gender;
    private Address address;

    public Builder() {}

    public Builder email(String email) {
      this.email = email;
      return this;
    }
    public Builder password(String password) {
      this.password = password;
      return this;
    }
    public Builder age(int age) {
      this.age = age;
      return this;
    }
    public Builder gender(Gender gender) {
      this.gender = gender;
      return this;
    }
    public Builder address(Address address) {
      this.address = address;
      return this;
    }

    public User build() {
      return new User(this);
    }
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
