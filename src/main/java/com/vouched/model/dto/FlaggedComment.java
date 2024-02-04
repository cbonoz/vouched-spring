package com.vouched.model.dto;

import java.util.Date;
import java.util.UUID;

public record FlaggedComment(UUID commentId, String message, String authorName, Date flaggedAt) {}
