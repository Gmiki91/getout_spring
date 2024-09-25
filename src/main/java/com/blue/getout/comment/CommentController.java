package com.blue.getout.comment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService){
        this.commentService=commentService;
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByEventId(@PathVariable String eventId){
        return commentService.getCommentsByEventId(eventId);
    }

    @PostMapping()
    public ResponseEntity<CommentResponse> addComment(@RequestBody CommentDTO commentDTO){
        return commentService.addComment(commentDTO);
    }
}
