package com.studytracker.note.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "notes")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Note {

    @Id
    private String id;

    private String title;
    private String content;
    private List<String> tasks;
    private String userId;
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
