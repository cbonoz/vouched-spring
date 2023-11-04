package com.vouched.model.dto;

import lombok.Data;

import javax.annotation.Nullable;
import java.util.UUID;

@Data
public class CommentRating {
    @Nullable UUID id;
    boolean upVote;
    UUID commentId;
}
