package com.example.lab10.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Note {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt = LocalDateTime.now();
    private Long userId; 
}
