package com.studytracker.note;


import com.studytracker.note.dto.ApiResponse;
import com.studytracker.note.dto.request.NoteRequest;
import com.studytracker.note.dto.response.NoteResponse;
import com.studytracker.note.entity.Note;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin(origins = "http://localhost:8888")
public class NoteController {

    NoteService noteService;

    @PostMapping("/create-note")
    public ApiResponse<NoteResponse> createNotes(
            @Valid @RequestBody NoteRequest request) {
        return ApiResponse.<NoteResponse>builder()
                .result(noteService.createNote(request))
                .build();
    }

    @GetMapping("/get-all-note")
    @Operation(summary = "Lấy tất cả")
    public ResponseEntity<List<Note>> getAllNotes(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false ) Integer limit
    ) {
        if (page == null) {
            page = 0;
        }
        if (limit == null) {
            limit = 20;
        }
        List<Note> notes = noteService.getAllNotes(page, limit);
        return ResponseEntity.ok(notes);
    }


    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật")
    ApiResponse<NoteResponse> updateNote(@PathVariable String id, @RequestBody NoteRequest request) {
        return ApiResponse.<NoteResponse>builder()
                .result(noteService.updateNote(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa ghi chú")
    public void deleteNotes(@PathVariable String id) {
        noteService.deleteNote(id);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Lấy ghi chú bằng id")
    public ResponseEntity<NoteResponse> getNoteById(@PathVariable("id") String id) {
        NoteResponse schedule = noteService.getNoteById(id);
        return ResponseEntity.ok(schedule);
    }

    @GetMapping("/get-note-by/{userId}")
    public List<NoteResponse> getNotesByUserId(@PathVariable String userId) {
        return noteService.getNoteByUserId(userId);
    }
}
