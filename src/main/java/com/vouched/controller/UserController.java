package com.vouched.controller;

import com.vouched.annotation.CurrentUser;
import com.vouched.auth.UserToken;
import com.vouched.dao.UserDao;
import com.vouched.model.domain.VouchedUser;
import com.vouched.service.email.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;

import static com.vouched.util.StringUtil.isValidEmail;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserDao userDao;
    private EmailService emailService;

    @Inject
    public UserController(UserDao userDao, EmailService emailService) {
        this.userDao = userDao;
        this.emailService = emailService;
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
        return optionalUser.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get user by token
    @GetMapping("/me")
    public ResponseEntity<UserToken> getUser(@CurrentUser UserToken user) {
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/invite")
    public ResponseEntity<String> inviteUser(@CurrentUser UserToken user, @RequestParam("email") String email) {
        if (!isValidEmail(email)) {
            return ResponseEntity.badRequest().build();
        }
        Map<String, Object> templateMap = Map.of();
        emailService.sendUserInvite(email, templateMap);


        return ResponseEntity.ok("Invited user with email: " + email);
    }

    @PostMapping("/request")
    public ResponseEntity<String> requestUser(@CurrentUser UserToken user, @RequestParam("email") String email) {
        if (!isValidEmail(email)) {
            return ResponseEntity.badRequest().build();
        }
        Map<String, Object> templateMap = Map.of();
        emailService.sendUserRequest(email, templateMap);

        return ResponseEntity.ok("Requested user with email: " + email);
    }

}
