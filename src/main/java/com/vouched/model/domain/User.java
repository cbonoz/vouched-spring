package com.vouched.model.domain;

import java.util.Date;
import java.util.UUID;

public record User(
        UUID id,
        String externalId,
        String email,
        String handle,
        Date activatedAt,
        Date createdAt,
        Date updatedAt) {
};