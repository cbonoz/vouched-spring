package com.vouched.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.vouched.error.SoftException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
class UserInputServiceTest {

  private UserInputService userInputService;

  @BeforeEach
  void setUp() {
    userInputService =
        new UserInputService(new DefaultResourceLoader());
  }

  @Test
  void validateCommentSplitOk() {
    String comment = "This is a comment. This is a long enough comment";
    userInputService.validateComment(comment);
  }

  @Test
  void validateCommentSplitShort() {
    String comment = "too short comment.";
    assertThrows(
        SoftException.class,
        () -> {
          userInputService.validateComment(comment);
        });
  }

  @Test
  void validateCommentSplitEmpty() {
    String comment = "";
    assertThrows(
        SoftException.class,
        () -> {
          userInputService.validateComment(comment);
        });
  }

  @Test
  void validateCommentSplitInvalid() {
    assertThrows(
        SoftException.class,
        () -> {
          String comment = "shit";
          userInputService.validateComment(comment);
        });
  }
}
