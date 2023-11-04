package com.vouched.controller;

import com.vouched.annotation.CurrentUser;
import com.vouched.auth.UserToken;
import com.vouched.dao.EndorsementDao;
import com.vouched.dao.UserDao;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
@RequestMapping("/users")
public class ProfileController {

    private final UserDao userDao;
    private final EndorsementDao endorsementDao;

    @Inject
    public ProfileController(UserDao userDao, EndorsementDao endorsementDao) {
        this.userDao = userDao;
        this.endorsementDao = endorsementDao;
    }

    // Get user by token
    @GetMapping("/me")
    public ResponseEntity<UserToken> getUser(@CurrentUser UserToken user) {
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

 }
