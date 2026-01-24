package com.example.lab10.repository;

import com.example.lab10.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setDetails(rs.getString("details")); // JSON-u oxu
        return user;
    };

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UserRepository.class);

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        // PII Silindi
        logger.debug("UserRepository: Fetching user records from database for email: {}", email);
        return jdbcTemplate.query(sql, userRowMapper, email).stream().findFirst();
    }

    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        return jdbcTemplate.query(sql, userRowMapper, id).stream().findFirst();
    }

    public void save(User user) {
        String sql = "INSERT INTO users (username, email, password, role, details) VALUES (?, ?, ?, ?, ?)";
        // Burada "details" hissəsinə əl ilə JSON string göndərə bilərik
        String jsonDetails = (user.getDetails() != null) ? user.getDetails()
                : "{\"city\":\"Baku\", \"registration_date\":\"2026-01-24\"}";

        logger.info("UserRepository: Saving user with JSON metadata for user: {}", user.getEmail());
        jdbcTemplate.update(sql, user.getUsername(), user.getEmail(), user.getPassword(), user.getRole(), jsonDetails);
    }
}
