package com.blue.getout.comment;

import java.time.ZonedDateTime;

public record CommentResponse(String id,String text, ZonedDateTime timestamp, String userName) {
}
