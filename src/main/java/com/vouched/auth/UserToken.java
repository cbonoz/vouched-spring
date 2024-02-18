package com.vouched.auth;

import java.util.UUID;

public record UserToken(
    UUID id,
    String externalId,
    String email,
    String imageUrl,
    String firstName,

    String lastName,
    String handle
) {

  // simple constructor
  public static UserToken createSuperUserToken(UUID id, String externalId, String email) {
    return new UserToken(id, email, null, externalId, "Super", "User", null);
  }

  public String getFullName() {
    return firstName + " " + lastName;
  }

}
