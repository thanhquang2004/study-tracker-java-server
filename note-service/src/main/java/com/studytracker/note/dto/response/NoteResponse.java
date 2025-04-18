package com.studytracker.note.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NoteResponse {
    private String id;
    private String title;
    private String content;
    private List<String> tasks;
    private String userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
