package com.rocket.domains.user.infrastructure.persistence;

import com.rocket.domains.user.application.dto.common.AddressDTO;
import com.rocket.domains.user.domain.entity.User;
import com.rocket.domains.user.domain.enums.Gender;
import com.rocket.domains.user.domain.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class UserJpaRepositoryImpl implements UserRepository {

  private final UserJpaRepository userJpaRepository;

  public UserJpaRepositoryImpl(UserJpaRepository userJpaRepository) {
    this.userJpaRepository = userJpaRepository;
  }

  @Override
  public User saveUser(User user) {
    return userJpaRepository.save(user);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return userJpaRepository.findByEmail(email);
  }

  @Override
  public List<User> findAll() {
    return userJpaRepository.findAll();
  }

  @Override
  public Boolean deleteUser(String email) {
    return userJpaRepository.deleteByEmail(email);
  }

  @Override
  public Boolean existsByEmail(String email) {
    return userJpaRepository.existsByEmail(email);
  }

  @Override
  public int updateAgeByEmail(String email, Integer age) {
    return userJpaRepository.updateAgeByEmail(age, email);
  }

  @Override
  public int updateGender(String email, Gender gender) {
    return userJpaRepository.updateGenderByEmail(gender, email);
  }

  @Override
  public int updateAddress(String email, AddressDTO address) {
    return userJpaRepository.updateAddressByEmail(address, email);
  }

  @Override
  public boolean existsById(Long id) {
    return userJpaRepository.existsById(id);
  }
}
