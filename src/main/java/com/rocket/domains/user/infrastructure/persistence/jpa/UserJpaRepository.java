package com.rocket.domains.user.infrastructure.persistence.jpa;

import com.rocket.domains.user.application.dto.request.AddressRequest;
import com.rocket.domains.user.domain.entity.User;
import com.rocket.domains.user.domain.enums.Gender;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserJpaRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(
      @NotBlank(message = "이메일은 필수 입력값입니다.")
      @Email(message = "올바른 이메일 형식이 아닙니다.")
      String email
  );

  Boolean deleteByEmail(
      @NotBlank(message = "이메일은 필수 입력값입니다.")
      @Email(message = "올바른 이메일 형식이 아닙니다.")
      String email);

  Boolean existsByEmail(
      @NotBlank(message = "이메일은 필수 입력값입니다.")
      @Email(message = "올바른 이메일 형식이 아닙니다.")
      String email
  );

  @Transactional
  @Query("update User u set u.age = :age where u.email = :email")
  @Modifying
  int updateAgeByEmail(
      @NotNull(message = "나이는 필수 입력값입니다.")
      @Min(value = 0, message = "나이는 음수일 수 없습니다.")
      int age,

      @NotBlank(message = "이메일은 필수 입력값입니다.")
      @Email(message = "올바른 이메일 형식이 아닙니다.")
      String email
  );

  @Transactional
  @Query("update User u set u.gender = :gender where u.email = :email")
  @Modifying
  int updateGenderByEmail(
      @NotNull(message = "성별은 필수 입력값입니다.")
      Gender gender,

      @NotBlank(message = "이메일은 필수 입력값입니다.")
      @Email(message = "올바른 이메일 형식이 아닙니다.")
      String email
  );

  @Transactional
  @Query("update User u set u.address = :address where u.email = :email")
  @Modifying
  int updateAddressByEmail(
      @NotNull(message = "주소는 필수 입력값입니다.")
      AddressRequest address,

      @NotBlank(message = "이메일은 필수 입력값입니다.")
      @Email(message = "올바른 이메일 형식이 아닙니다.")
      String email
  );

  Boolean existsByNickname(
      @NotBlank(message = "닉네임은 필수 입력값입니다.") @Size(max = 30, message = "닉네임은 최대 30자까지 입력 가능합니다.") String nickname);
}
