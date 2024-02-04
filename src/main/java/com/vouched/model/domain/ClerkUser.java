package com.vouched.model.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public record ClerkUser(
        String id,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        @JsonProperty("external_id") String externalId,
        @JsonProperty("image_url") String imageUrl,
        @JsonProperty("email_addresses") List<EmailEntry> emailAddresses,
        @JsonProperty("private_metadata") Map<String, Object> privateMetadata) {}
