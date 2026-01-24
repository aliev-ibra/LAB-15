package com.example.lab10.repository;

import com.example.lab10.model.Note;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class NoteRepository {

    private final JdbcTemplate jdbcTemplate;

    public NoteRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Note> noteRowMapper = (rs, rowNum) -> {
        Note note = new Note();
        note.setId(rs.getLong("id"));
        note.setTitle(rs.getString("title"));
        note.setContent(rs.getString("content"));
        note.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        note.setUserId(rs.getLong("user_id"));
        return note;
    };

    public List<Note> findByUserId(Long userId) {
        String sql = "SELECT * FROM notes WHERE user_id = ?";
        return jdbcTemplate.query(sql, noteRowMapper, userId);
    }

    public Optional<Note> findById(Long id) {
        String sql = "SELECT * FROM notes WHERE id = ?";
        return jdbcTemplate.query(sql, noteRowMapper, id).stream().findFirst();
    }

    public void save(Note note) {
        if (note.getId() == null) {
            String sql = "INSERT INTO notes (title, content, user_id) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, note.getTitle(), note.getContent(), note.getUserId());
        } else {
            // Update yalnız həmin istifadəçiyə aid qeydlər üçün işləyir
            String sql = "UPDATE notes SET title = ?, content = ? WHERE id = ? AND user_id = ?";
            jdbcTemplate.update(sql, note.getTitle(), note.getContent(), note.getId(), note.getUserId());
        }
    }

    public void deleteByIdAndUserId(Long id, Long userId) {
        String sql = "DELETE FROM notes WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, userId);
    }
}
