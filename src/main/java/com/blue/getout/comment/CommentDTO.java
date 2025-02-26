package com.blue.getout.comment;

import java.time.ZonedDateTime;
import java.util.UUID;

public record CommentDTO(String text, ZonedDateTime timestamp, UUID eventId, UUID userId) {}
