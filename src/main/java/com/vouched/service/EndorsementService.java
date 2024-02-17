package com.vouched.service;

import com.vouched.error.SoftException;
import com.vouched.model.dto.CreateEndorsementDto;
import javax.inject.Inject;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

@Service
public class EndorsementService {


  private final UserInputService userInputService;

  @Inject
  public EndorsementService(UserInputService userInputService) {
    this.userInputService = userInputService;
  }

  // Validate comment
  private void validateComment(String input) {
    if (Strings.isBlank(input)) {
      throw new SoftException("Text cannot be empty");
    }
    if (input.length() > 1000) {
      throw new SoftException("Text cannot be longer than 1000 characters");
    }
    userInputService.checkProfanity(input);
  }


  public void validateEndorsement(CreateEndorsementDto dto) {
    validateComment(dto.message());
    if (Strings.isBlank(dto.firstName())) {
      throw new SoftException("First name cannot be empty");
    }
    if (Strings.isBlank(dto.lastName())) {
      throw new SoftException("Last name cannot be empty");
    }
    if (Strings.isBlank(dto.relationship())) {
      throw new SoftException("Relationship cannot be empty");
    }
  }
}
