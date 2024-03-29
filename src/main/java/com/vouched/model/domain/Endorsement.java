package com.vouched.model.domain;

import java.util.Date;
import java.util.UUID;
import lombok.Data;

@Data
public class Endorsement {

  UUID id;
  UUID endorserId;
  String relationship;
  String skills;
  String firstName;
  String lastName;
  String message;
  Date createdAt;
}
