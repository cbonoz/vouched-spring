package com.vouched.model.dto;

public record CreateEndorsementDto(String message, String handle) {

    public CreateEndorsementDto {
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("message cannot be null");
        }
    }
}
