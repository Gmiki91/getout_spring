package com.blue.getout.comment;

import java.time.ZonedDateTime;
import java.util.UUID;

public record CommentResponse(UUID id, String text, ZonedDateTime timestamp, String userName, String userAvatarUrl) {
}
