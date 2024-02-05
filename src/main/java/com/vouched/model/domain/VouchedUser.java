package com.vouched.model.domain;

import java.util.Date;
import java.util.UUID;

public record VouchedUser(
    UUID id,
    String externalId,
    String email,
    String firstName,
    String lastName,
    String imageUrl,
    String handle,
    Date activatedAt,
    Date createdAt,
    Date updatedAt) {

  public String getFullName() {
    return firstName + " " + lastName;
  }

}
