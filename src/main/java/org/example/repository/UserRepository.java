package org.example.repository;
import org.apache.commons.lang3.NotImplementedException;
import org.example.model.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(User user) {
        String sql = "INSERT INTO users (login, password, full_name) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, user.getLogin(), user.getPassword(), user.getFullName());
    }

    public boolean existsByLogin(String login) {
        String sql = "SELECT COUNT(*) FROM users WHERE login = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, login);
        return count > 0;
    }

    public Optional<User> findById(Long id) {
        try {
            String sql = "SELECT * FROM users WHERE id = ?";
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new UserRowMapper(), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<UserDetails> findByLogin(String username) {
        String sql = "SELECT id, login, password, full_name FROM users WHERE login = ?";

        try {
            User user = jdbcTemplate.queryForObject(sql, new UserRowMapper(), username);

            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setLogin(rs.getString("login"));
            user.setPassword(rs.getString("password"));
            user.setFullName(rs.getString("full_name"));
            return user;
        }
    }
}
