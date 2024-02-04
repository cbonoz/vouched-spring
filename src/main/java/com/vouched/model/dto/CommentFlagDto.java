package com.vouched.model.dto;

import java.util.UUID;
import javax.annotation.Nullable;
import lombok.Data;

@Data
public class CommentFlagDto {
    UUID commentId;
    @Nullable String reason;
}
