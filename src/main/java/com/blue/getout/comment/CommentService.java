package com.blue.getout.comment;

import com.blue.getout.notification.NotificationService;
import com.blue.getout.utils.Mapper;
import com.blue.getout.event.Event;
import com.blue.getout.event.EventRepository;
import com.blue.getout.user.User;
import com.blue.getout.user.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final NotificationService notificationService;
    private final Mapper mapper;

    public CommentService(EventRepository eventRepository, UserRepository userRepository, CommentRepository commentRepository, NotificationService notificationService, Mapper mapper) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.notificationService=notificationService;
        this.mapper = mapper;
    }

    public ResponseEntity<CommentResponse> addComment(CommentDTO commentDTO) {
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
        this.notificationService.updateCommentNotification(event,user.getId());
        CommentResponse commentResponse= mapper.CommentEntityToResponse(comment);
        return ResponseEntity.ok(commentResponse);
    }

    public ResponseEntity<List<CommentResponse>> getCommentsByEventId(String eventId) {
        List<CommentResponse> comments = commentRepository.findByEventId(eventId, Sort.by(Sort.Direction.DESC, "timestamp"))
                .stream()
                .map(mapper::CommentEntityToResponse)
                .toList();

        return ResponseEntity.ok(comments);
    }
}
