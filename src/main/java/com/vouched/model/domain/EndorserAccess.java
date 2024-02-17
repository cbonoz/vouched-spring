package com.vouched.model.domain;

import java.util.Date;
import java.util.UUID;
import lombok.Data;

@Data
public class EndorserAccess {

  UUID id;
  UUID endorserId;
  UUID requesterId;
  String requesterEmail;
  String message;
  Date createdAt;
  Date approvedAt;
  Date deletedAt;

}
