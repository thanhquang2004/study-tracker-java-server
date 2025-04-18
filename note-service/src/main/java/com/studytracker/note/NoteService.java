package com.studytracker.note;

import com.studytracker.note.dto.request.NoteRequest;
import com.studytracker.note.dto.response.NoteResponse;
import com.studytracker.note.entity.Note;

import com.studytracker.note.exception.AppException;
import com.studytracker.note.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class NoteService {

    NoteRepository noteRepository;
    NoteMapper noteMapper;

    public NoteResponse createNote(NoteRequest request) {
        Note note = noteMapper.toNote(request);
        note = noteRepository.save(note);
        return noteMapper.toNoteResponse(note);
    }

    public NoteResponse getNoteById(String id) {
         Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        return noteMapper.toNoteResponse(note);
    }

    public List<Note> getAllNotes(int page, int limit) {
        PageRequest pageRequest = PageRequest.of(page, limit);
        Page<Note> categoryPage = noteRepository.findAll(pageRequest);
        return categoryPage.getContent();
    }

    public List<NoteResponse> getNoteByUserId(String userId) {
        List<Note> notes = noteRepository.findByUserId(userId);
        return noteMapper.toNoteResponseList(notes);
    }

    public NoteResponse updateNote(String id,
                               NoteRequest request) {
        Note note = noteRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOTE_NOT_EXISTED));

        noteMapper.updateNote(note, request);
        return noteMapper.toNoteResponse(noteRepository.save(note));
    }

    public void deleteNote(String id) {
        noteRepository.deleteById(id);
    }


}
