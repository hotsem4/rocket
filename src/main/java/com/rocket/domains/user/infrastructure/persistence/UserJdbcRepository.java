package com.rocket.domains.user.infrastructure.persistence;

import com.rocket.domains.user.application.dto.common.AddressDTO;
import com.rocket.domains.user.domain.entity.User;
import com.rocket.domains.user.domain.enums.Gender;
import com.rocket.domains.user.domain.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserJdbcRepository implements UserRepository{

  private final JdbcTemplate jdbcTemplate;

  public UserJdbcRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public Boolean saveUser(User user) {
    String sql = "INSERT INTO users (email, password, age, gender, street, CITY, STATE, ZIP_CODE) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    try {
      jdbcTemplate.update(
          sql,
          user.getEmail(),
          user.getPassword(),
          user.getAge(),
          user.getGender().name(),
          user.getAddress().street(),
          user.getAddress().city(),
          user.getAddress().state(),
          user.getAddress().zipCode()
      );
      return true;
    } catch (Exception e) {
      System.out.println("e = " + e);
      return false;
    }
  }

  @Override
  public Optional<User> findByEmail(String email) {
    String sql = "SELECT * FROM users WHERE email = ?";
    try{
      User user = jdbcTemplate.queryForObject(sql, new UserRowMapper(), email);
      return Optional.ofNullable(user);
    } catch (EmptyResultDataAccessException e){
      System.out.println("e = " + e);
      return Optional.empty();
    }
  }

  /**
   * 대량의 데이터에는 부적합함.
   * 대량 데이터 조회 구현시 페이징 고려
   */
  @Override
  public List<User> findAll() {
    String sql = "SELECT * FROM users";
    return jdbcTemplate.query(sql, new UserRowMapper());
  }

  @Override
  public Boolean deleteUser(String email) {
    String sql = "DELETE FROM users WHERE email = ?";
    int rowAffected = jdbcTemplate.update(sql, email);
    return rowAffected > 0; // 0보다 크면 성공, 0이면 실패
  }

  @Override
  public Boolean existsByEmail(String email) {
    String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
    return count != null && count > 0;
  }

  @Override
  public void updateAge(String email, Integer age) {
    String sql = "UPDATE users SET age = ? WHERE email = ?";
    jdbcTemplate.update(sql, age, email);
  }

  @Override
  public void updateGender(String email, Gender gender) {
    String sql = "UPDATE users SET gender = ? WHERE email = ?";
    jdbcTemplate.update(sql, gender.name(), email);
  }

  @Override
  public void updateAddress(String email, AddressDTO address) {
    String sql = "UPDATE users SET STATE = ?, CITY = ?, STREET = ?, ZIP_CODE = ? WHERE email = ?";
    jdbcTemplate.update(sql,
        address.state(),
        address.city(),
        address.street(),
        address.zipCode(),
        email
    );
  }

  @Override
  public boolean existsById(Long id) {
    String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    return count != null && count > 0;
  }
}
