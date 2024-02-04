package com.vouched.service;

import com.vouched.config.AppProperties;
import com.vouched.dao.UserDao;
import com.vouched.error.SoftException;
import com.vouched.service.email.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
class EndorsementServiceTest {

    EndorsementService endorsementService;

    @Mock
    EmailService emailService;
    @Mock
    UserDao userDao;
    @Mock
    AppProperties appProperties;

    @BeforeEach
    void setUp() {
        endorsementService =
                new EndorsementService(
                        new DefaultResourceLoader(), userDao, emailService, appProperties);
    }

    @Test
    void isTrue() {
        assertTrue(true);
    }

    @Test
    void validateCommentSplitOk() {
        String comment = "This is a comment";
        endorsementService.validateComment(comment);
    }

    @Test
    void validateCommentSplitOk2() {
        String comment = "an al";
        endorsementService.validateComment(comment);
    }

    @Test
    void validateCommentSplitEmpty() {
        assertThrows(
                SoftException.class,
                () -> {
                    String comment = "";
                    endorsementService.validateComment(comment);
                });
    }

    @Test
    void validateCommentSplitInvalid() {
        assertThrows(
                SoftException.class,
                () -> {
                    String comment = "shit";
                    endorsementService.validateComment(comment);
                });
    }
}
