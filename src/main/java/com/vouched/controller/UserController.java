package com.vouched.controller;

import static com.vouched.util.StringUtil.isValidEmail;

import com.vouched.annotation.CurrentUser;
import com.vouched.auth.UserToken;
import com.vouched.dao.UserDao;
import com.vouched.error.SoftException;
import com.vouched.model.domain.ClerkUpdateUserRequest;
import com.vouched.model.domain.UpdateUserRequest;
import com.vouched.model.domain.VouchedUser;
import com.vouched.model.dto.UserWebhookEvent;
import com.vouched.model.dto.UserWebhookEvent.Data;
import com.vouched.model.param.UserInvite;
import com.vouched.model.param.UserRequest;
import com.vouched.service.RateLimiterService;
import com.vouched.service.email.EmailService;
import java.util.Optional;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

  private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

  private final UserDao userDao;
  private final EmailService emailService;
  private final RateLimiterService rateLimiterService;

  @Inject
  public UserController(UserDao userDao, EmailService emailService,
      RateLimiterService rateLimiterService) {
    this.userDao = userDao;
    this.emailService = emailService;
    this.rateLimiterService = rateLimiterService;
  }

  @GetMapping("")
  public ResponseEntity<VouchedUser> getUser(@RequestParam("email") String email,
      @RequestParam("handle") String handle) {
    Optional<VouchedUser> optionalUser;
    if (isValidEmail(email)) {
      optionalUser = userDao.getUserByEmail(email);
    } else if (handle != null && !handle.isEmpty()) {
      optionalUser = userDao.getUserByHandle(handle);
    } else {
      return ResponseEntity.badRequest().build();
    }
    return optionalUser.map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PatchMapping("/clerk/webhook")
  public ResponseEntity<ClerkUpdateUserRequest> updateUser(
      @RequestBody UserWebhookEvent userWebhookEvent
  ) {
    LOG.info("Received user webhook event: {} {}", userWebhookEvent.type(),
        userWebhookEvent.data().id());
    if (!"user.updated".equals(userWebhookEvent.type())) {
      return ResponseEntity.ok().build();
    }

    Data data = userWebhookEvent.data();
    ClerkUpdateUserRequest clerkUpdateUserRequest = new ClerkUpdateUserRequest(
        data.id(),
        data.first_name(),
        data.last_name(),
        data.profile_image_url()
    );
    userDao.updateUser(clerkUpdateUserRequest);
    return ResponseEntity.ok(clerkUpdateUserRequest);
  }

  // Get user by token
  @GetMapping("/me")
  public ResponseEntity<VouchedUser> getUser(@CurrentUser UserToken user) {
    if (user == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(userDao.getUserById(user.id()).orElseThrow());
  }

  @PatchMapping("/me")
  public ResponseEntity<VouchedUser> updateUser(@CurrentUser UserToken user,
      @RequestBody UpdateUserRequest updateUserRequest) {
    updateUserRequest.setId(Optional.of(user.id()));
    String currentImageUrl = updateUserRequest.getImageUrl();
    if (currentImageUrl == null || !currentImageUrl.equals(user.imageUrl())) {
      updateUserRequest.setImageUrl(user.imageUrl());
    }
    userDao.updateUser(updateUserRequest);
    return ResponseEntity.ok(userDao.getUserById(user.id()).orElseThrow());
  }

  @PostMapping("/invite")
  public ResponseEntity<String> inviteUser(@CurrentUser UserToken user,
      @RequestBody UserRequest userRequest
  ) {
    if (!isValidEmail(userRequest.email())) {
      throw new SoftException("Invalid email: " + userRequest.email());
    }

    rateLimiterService.recordUserRequest();

    UserInvite userInvite = new UserInvite(userRequest.name(), userRequest.email(),
        user.getFullName());
    emailService.sendUserInvite(userInvite);
    return ResponseEntity.ok("Invited user with email: " + userRequest.email());
  }

  @PostMapping("/request")
  public ResponseEntity<String> requestUser(
      @RequestBody UserRequest userRequest) {
    if (!isValidEmail(userRequest.email())) {
      throw new SoftException("Invalid email: " + userRequest.email());
    }
    rateLimiterService.recordUserRequest();
    emailService.sendUserRequest(userRequest);

    return ResponseEntity.ok("Requested user with email: " + userRequest.email());
  }

}
