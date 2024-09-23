package com.blue.getout.comment;

import java.time.ZonedDateTime;

public record CommentDTO(String id,String text,ZonedDateTime timestamp,String eventId,String userId) {}
