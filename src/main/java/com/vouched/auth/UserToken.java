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

  public static final String SUPER_USER_EXTERNAL_ID = "-1";

  // simple constructor
  public static UserToken createSuperUserToken(UUID id, String email) {
    return new UserToken(id, SUPER_USER_EXTERNAL_ID, email, null, "Super", "User", null);
  }

  public String getFullName() {
    return firstName + " " + lastName;
  }

}
