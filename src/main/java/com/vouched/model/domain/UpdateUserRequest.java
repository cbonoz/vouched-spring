package com.vouched.model.domain;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import lombok.Data;

@Data
public class UpdateUserRequest {

  Optional<UUID> id;

  String firstName;
  String lastName;
  String handle;
  String title;
  String bio;
  String agreementText;
  String imageUrl;
  Date activatedAt;


}
