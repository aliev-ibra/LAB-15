package com.example.lab10.controller;

import com.example.lab10.dto.NoteDTO;
import com.example.lab10.service.NoteService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());
        model.addAttribute("notes", noteService.getMyNotes());
        return "dashboard";
    }

    @PostMapping("/dashboard")
    public String createNote(@ModelAttribute NoteDTO noteDTO) {
        noteService.createNote(noteDTO);
        return "redirect:/dashboard";
    }

    @PostMapping("/dashboard/delete/{id}")
    public String deleteNote(@PathVariable Long id) {
        noteService.deleteNote(id);
        return "redirect:/dashboard";
    }
}
