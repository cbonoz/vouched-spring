package com.vouched.auth;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public record UserToken(
        UUID id,
        String email,
        String imageUrl,
        String externalId,
        String name,
        String handle,

        Date activatedAt,
        Map<String, Object> metadata
) {
    // simple constructor
    public static UserToken createEmailUser(UUID id, String externalId, String email) {
        return new UserToken(id, email, null, externalId, null, null, null, null);
    }
}
