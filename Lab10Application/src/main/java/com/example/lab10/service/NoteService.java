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
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Note createNote(NoteDTO noteDTO) {
        User user = getCurrentUser();
        Note note = new Note();
        note.setTitle(noteDTO.getTitle());
        note.setContent(noteDTO.getContent());
        note.setCreatedAt(LocalDateTime.now());
        note.setUser(user);
        return noteRepository.save(note);
    }

    public List<Note> getMyNotes() {
        User user = getCurrentUser();
        return noteRepository.findNotesByUserIdRaw(user.getId());
    }

    public Note getNoteById(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        
        User currentUser = getCurrentUser();
        if (!note.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to access this note");
        }
        return note;
    }

    @Transactional
    public Note updateNote(Long id, NoteDTO noteDTO) {
        Note note = getNoteById(id); // Checks ownership
        note.setTitle(noteDTO.getTitle());
        note.setContent(noteDTO.getContent());
        return noteRepository.save(note);
    }

    @Transactional
    public void deleteNote(Long id) {
        Note note = getNoteById(id); // Checks ownership
        noteRepository.delete(note);
    }
}
