package com.rocket.domains.user.domain.repository;

import com.rocket.domains.user.application.dto.common.AddressDTO;
import com.rocket.domains.user.domain.entity.User;
import com.rocket.domains.user.domain.enums.Gender;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
  User saveUser(User user);

  Optional<User> findByEmail(String email);
  List<User> findAll();
  Boolean deleteUser(String email);

  Boolean existsByEmail(String email);
  int updateAgeByEmail(String email, Integer age);
  int updateGender(String email, Gender gender);
  int updateAddress(String email, AddressDTO address);

  boolean existsById(Long id);
}
