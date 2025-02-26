package com.rocket.domains.user.domain.repository;

import com.rocket.domains.user.application.dto.common.AddressDTO;
import com.rocket.domains.user.domain.entity.User;
import com.rocket.domains.user.domain.enums.Gender;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {
  Boolean saveUser(User user);

  Optional<User> findByEmail(String email);
  List<User> findAll();
  Boolean deleteUser(String email);

  Boolean existsByEmail(String email);
  void updateAge(String email, Integer age);
  void updateGender(String email, Gender gender);
  void updateAddress(String email, AddressDTO address);

  boolean existsById(Long id);
}
