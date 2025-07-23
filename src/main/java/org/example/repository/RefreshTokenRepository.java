package org.example.repository;

import org.example.model.RefreshToken;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class RefreshTokenRepository {
    private final JdbcTemplate jdbcTemplate;

    public RefreshTokenRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(RefreshToken refreshToken) {
        String sql = "INSERT INTO refresh_tokens (user_id, token, expiry_date) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql,
                refreshToken.getUserId(),
                refreshToken.getToken(),
                Timestamp.valueOf(refreshToken.getExpiryDate()));
    }

    public Optional<RefreshToken> findByToken(String token) {
        String sql = "SELECT * FROM refresh_tokens WHERE token = ?";
        try {
            RefreshToken refreshToken = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                RefreshToken rt = new RefreshToken();
                rt.setId(rs.getInt("id"));
                rt.setUserId(rs.getInt("user_id"));
                rt.setToken(rs.getString("token"));
                rt.setExpiryDate(rs.getTimestamp("expiry_date").toLocalDateTime());
                return rt;
            }, token);
            return Optional.ofNullable(refreshToken);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void deleteByUserId(Integer userId) {
        String sql = "DELETE FROM refresh_tokens WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }
}