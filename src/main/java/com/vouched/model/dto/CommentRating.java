package com.vouched.model.dto;

import java.util.UUID;
import javax.annotation.Nullable;
import lombok.Data;

@Data
public class CommentRating {
    @Nullable UUID id;
    boolean upVote;
    UUID commentId;
}
