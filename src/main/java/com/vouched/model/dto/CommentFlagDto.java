package com.vouched.model.dto;

import lombok.Data;

import javax.annotation.Nullable;
import java.util.UUID;

@Data
public class CommentFlagDto {
        UUID commentId;
        @Nullable String reason;
}
