package com.rocket.domains.posts.infrastructure.persistence;

import com.rocket.domains.posts.domain.entity.Post;
import com.rocket.domains.posts.domain.enums.repository.PostRepository;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class PostJdbcRepository implements PostRepository {
  public final JdbcTemplate jdbcTemplate;

  public PostJdbcRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }


  @Override
  public Post savePost(Post post) {
    String sql = "INSERT INTO posts (title, content, author_id, created_at, like_count) VALUES (?, ?, ?, NOW(), ?)";
    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(sql,
          new String[] {"id"});
      ps.setString(1, post.getTitle());
      ps.setString(2, post.getContent());
      ps.setLong(3, post.getAuthorId());
      ps.setInt(4, post.getLikeCount());
      return ps;
    }, keyHolder);

    Long generatedId = keyHolder.getKey().longValue();

    return findById(generatedId).orElseThrow(() -> new IllegalArgumentException(
        "게시글 저장 후 데이터를 찾을 수 없습니다."));
  }

  public Optional<Post> findById(Long id) {
    String sql = "SELECT id, title, content, author_id, created_at, like_count FROM posts WHERE id = ?";
    return jdbcTemplate.query(sql, new Object[]{id}, rs -> {
      if (rs.next()) {
        return Optional.of(new Post(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getString("content"),
            rs.getLong("author_id"),
            rs.getTimestamp("created_at").toLocalDateTime()
        ));
      }
      return Optional.empty();
    });
  }

  @Override
  public List<Post> findByTitle(String title) {
    String sql = "SELECT id, title, content, author_id, created_at, like_count " +
        "FROM posts WHERE LOWER(title) LIKE LOWER(?)";

    return jdbcTemplate.query(sql, new Object[]{"%" + title + "%"}, (rs, rowNum) ->
        new Post(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getString("content"),
            rs.getLong("author_id"),
            rs.getTimestamp("created_at").toLocalDateTime()
        )
    );
  }


  @Override
  public List<Post> findAll() {
    String sql = "SELECT id, title, content, author_id, created_at, like_count FROM posts";
    return jdbcTemplate.query(sql, (rs, rowNum) -> new Post(
        rs.getLong("id"),
        rs.getString("title"),
        rs.getString("content"),
        rs.getLong("author_id"),
        rs.getTimestamp("created_at").toLocalDateTime()
    ));
  }


  @Override
  public Boolean deleteById(Long id) {
    String sql = "DELETE FROM posts WHERE id = ?";
    int result = jdbcTemplate.update(sql, id);
    return result > 0;
  }

  @Override
  public Boolean updateById(Long id, String title, String content) {
    // 기존 데이터 조회
    String selectSql = "SELECT title, content FROM posts WHERE id = ?";
    return jdbcTemplate.query(selectSql, new Object[]{id}, rs -> {
      if (rs.next()) {
        // 기존 값 유지
        String currentTitle = rs.getString("title");
        String currentContent = rs.getString("content");

        String newTitle = (title != null) ? title : currentTitle;
        String newContent = (content != null) ? content : currentContent;

        // 업데이트 실행
        String updateSql = "UPDATE posts SET title = ?, content = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(updateSql, newTitle, newContent, id);
        return rowsAffected > 0; // 업데이트 성공 시 true 반환
      }
      return false; // 해당 ID가 존재하지 않으면 false 반환
    });
  }

  @Override
  public Boolean incrementLikeCount(Long id) {
    String sql = "UPDATE posts SET like_count = like_count + 1 WHERE id = ?";
    int rowsAffected = jdbcTemplate.update(sql, id);
    return rowsAffected > 0;
  }


}
