package com.rocket.domains.user.infrastructure.persistence;

import com.rocket.domains.user.domain.entity.Address;
import com.rocket.domains.user.domain.entity.User;
import com.rocket.domains.user.domain.enums.Gender;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class UserRowMapper implements RowMapper<User> {

  @Override
  public User mapRow(ResultSet rs, int rowNum) throws SQLException {
    return new User( // 조회 시 id 포함된 생성자 사용
        rs.getLong("id"),
        rs.getString("email"),
        rs.getString("password"),
        rs.getInt("age"),
        Gender.fromString(rs.getString("gender")),
        new Address(
            rs.getString("state"),
            rs.getString("city"),
            rs.getString("street"),
            rs.getString("zip_code")
        )
    );
  }
}
