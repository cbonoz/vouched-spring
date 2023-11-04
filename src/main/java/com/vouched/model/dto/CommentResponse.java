package com.vouched.model.dto;

import lombok.Data;

@Data
public class CommentResponse {
    String id;
    String authorId;
    int upVotes;
    int downVotes;
    boolean hasUpvoted;
    boolean hasDownvoted;
    String authorName;
    String targetUserId;
    String message;
    String createdAt;
    String updatedAt;
}
