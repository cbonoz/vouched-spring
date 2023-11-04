package com.vouched.model.dto;

public record CreateEndorsementDto(String message, String email, String name) {

    public CreateEndorsementDto {
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("message cannot be null");
        }
    }
}
