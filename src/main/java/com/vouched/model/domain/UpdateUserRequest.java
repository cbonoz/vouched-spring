package com.vouched.model.domain;

import java.util.Optional;
import java.util.UUID;
import lombok.Data;

@Data
public class UpdateUserRequest {

  Optional<UUID> id;
  Optional<String> externalId;

  String firstName;
  String lastName;
  String handle;
  String title;
  String bio;
  String agreementText;
  String imageUrl;
  long activatedAt;


}
