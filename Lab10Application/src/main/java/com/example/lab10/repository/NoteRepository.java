package com.example.lab10.repository;

import com.example.lab10.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUserId(Long userId);

    @Query(value = "SELECT * FROM notes WHERE user_id = :userId", nativeQuery = true)
    List<Note> findNotesByUserIdRaw(@Param("userId") Long userId);
}
