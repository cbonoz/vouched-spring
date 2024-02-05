package com.vouched.model.domain;

import java.util.Date;
import java.util.UUID;
import lombok.Data;

@Data
public class Endorsement {

  UUID id;
  UUID userId;
  UUID endorserId;
  String message;
  Date createdAt;
}
