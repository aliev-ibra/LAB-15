package com.example.lab10.service;

import com.example.lab10.dto.NoteDTO;
import com.example.lab10.model.Note;
import com.example.lab10.model.User;
import com.example.lab10.repository.NoteRepository;
import com.example.lab10.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public NoteService(NoteRepository noteRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    public void createNote(NoteDTO noteDTO) {
        User user = getCurrentUser();
        Note note = new Note();
        note.setTitle(noteDTO.getTitle());
        note.setContent(noteDTO.getContent());
        note.setCreatedAt(LocalDateTime.now());
        note.setUserId(user.getId());
        noteRepository.save(note);
    }

    public List<Note> getMyNotes() {
        User user = getCurrentUser();
        return noteRepository.findByUserId(user.getId());
    }

    public Note getNoteById(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        
        User currentUser = getCurrentUser();
        if (!note.getUserId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to access this note");
        }
        return note;
    }

    public void updateNote(Long id, NoteDTO noteDTO) {
        Note note = getNoteById(id); // Checks ownership
        note.setTitle(noteDTO.getTitle());
        note.setContent(noteDTO.getContent());
        noteRepository.save(note);
    }

    public void deleteNote(Long id) {
        Note note = getNoteById(id); // Checks ownership
        noteRepository.deleteById(note.getId());
    }
}
