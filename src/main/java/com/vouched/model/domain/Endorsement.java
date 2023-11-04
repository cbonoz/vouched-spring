package com.vouched.model.domain;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class Endorsement {
    UUID id;
    UUID userId;
    UUID endorserId;
    String message;
    Date createdAt;
    Date updatedAt;
}
