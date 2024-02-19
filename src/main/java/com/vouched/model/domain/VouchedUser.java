package com.vouched.model.domain;

import java.util.Date;
import java.util.UUID;
import lombok.Data;

@Data
public class VouchedUser {

  UUID id;
  String externalId;
  String email;
  String firstName;
  String lastName;
  String imageUrl;
  long activatedAt;
  Date createdAt;
  Date updatedAt;

  String handle;
  String title;
  String bio;
  String agreementText;

}
