package com.vouched.model.domain;

public record ClerkUpdateUserRequest(String externalId, String firstName, String lastName,
                                     String imageUrl) {

}
