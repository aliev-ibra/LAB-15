package com.example.lab10.repository;

import com.example.lab10.model.RefreshToken;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
public class RefreshTokenRepository {
    private final JdbcTemplate jdbcTemplate;

    public RefreshTokenRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<RefreshToken> rowMapper = (rs, rowNum) -> RefreshToken.builder()
            .id(rs.getLong("id"))
            .token(rs.getString("token"))
            .userId(rs.getLong("user_id"))
            .expiryDate(rs.getTimestamp("expiry_date").toInstant())
            .build();

    public Optional<RefreshToken> findByToken(String token) {
        String sql = "SELECT * FROM refresh_tokens WHERE token = ?";
        return jdbcTemplate.query(sql, rowMapper, token).stream().findFirst();
    }

    public RefreshToken save(RefreshToken refreshToken) {
        if (refreshToken.getId() == null) {
            String sql = "INSERT INTO refresh_tokens (token, user_id, expiry_date) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, refreshToken.getToken(), refreshToken.getUserId(),
                    Timestamp.from(refreshToken.getExpiryDate()));
            // In a real app we might want to fetch the ID back, but for now this is okay or
            // we can query it back if needed.
            // For simplicity in this lab, we just return the object.
        } else {
            String sql = "UPDATE refresh_tokens SET token = ?, expiry_date = ? WHERE id = ?";
            jdbcTemplate.update(sql, refreshToken.getToken(), Timestamp.from(refreshToken.getExpiryDate()),
                    refreshToken.getId());
        }
        return refreshToken;
    }

    public void deleteByUserId(Long userId) {
        String sql = "DELETE FROM refresh_tokens WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }

    public void delete(RefreshToken refreshToken) {
        String sql = "DELETE FROM refresh_tokens WHERE id = ?";
        jdbcTemplate.update(sql, refreshToken.getId());
    }
}
