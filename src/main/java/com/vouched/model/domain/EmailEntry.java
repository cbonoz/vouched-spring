package com.vouched.model.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EmailEntry(

        @JsonProperty("email_address") String emailAddress
) {
}
