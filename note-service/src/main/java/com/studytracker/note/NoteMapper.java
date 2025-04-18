package com.studytracker.note;



import com.studytracker.note.dto.request.NoteRequest;
import com.studytracker.note.dto.response.NoteResponse;
import com.studytracker.note.entity.Note;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NoteMapper {

    Note toNote(NoteRequest request);

    NoteResponse toNoteResponse(Note schedule);

    void updateNote(@MappingTarget Note schedule, NoteRequest request);
    List<NoteResponse> toNoteResponseList(List<Note> schedules);
}
