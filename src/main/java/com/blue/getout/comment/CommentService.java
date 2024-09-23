package com.blue.getout.comment;

import com.blue.getout.Mapper;
import com.blue.getout.event.Event;
import com.blue.getout.event.EventRepository;
import com.blue.getout.user.User;
import com.blue.getout.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final Mapper mapper;

    public CommentService(EventRepository eventRepository, UserRepository userRepository, CommentRepository commentRepository, Mapper mapper) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.mapper = mapper;
    }

    public void addComment(CommentDTO commentDTO) {
        Event event = eventRepository.findById(commentDTO.eventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        User user = userRepository.findById(commentDTO.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = new Comment(
                commentDTO.timestamp(),
                commentDTO.text(),
                user,
                event
        );
        commentRepository.save(comment);
    }

    public ResponseEntity<List<CommentDTO>> getCommentsByEventId(String eventId) {
        List<CommentDTO> comments = commentRepository.findByEventId(eventId)
                .stream()
                .map(mapper::CommentEntityToDTO)
                .toList();


        return ResponseEntity.ok(comments);
    }
}
