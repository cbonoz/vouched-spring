package com.vouched.controller;

import com.vouched.annotation.CurrentUser;
import com.vouched.auth.UserToken;
import com.vouched.dao.EndorsementDao;
import com.vouched.dao.UserDao;
import com.vouched.error.SoftException;
import com.vouched.model.domain.Endorsement;
import com.vouched.model.domain.User;
import com.vouched.model.dto.CreateEndorsementDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

import static com.vouched.util.StringUtil.isValidEmail;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserDao userDao;
    private final EndorsementDao endorsementDao;

    @Inject
    public UserController(UserDao userDao, EndorsementDao endorsementDao) {
        this.userDao = userDao;
        this.endorsementDao = endorsementDao;
    }


    @GetMapping("")
    public ResponseEntity<User> getUser(@CurrentUser UserToken user, @RequestParam("email") String email) {
        // check if valid email
        if (!isValidEmail(email)) {
            return ResponseEntity.badRequest().build();
        }
        Optional<User> optionalUser = userDao.getUserByEmail(email);
        return optionalUser.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping("/endorse")
    public ResponseEntity<Endorsement> endorseUser(@RequestBody CreateEndorsementDto dto, @CurrentUser UserToken user) {
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        Optional<User> optionalUserToEndorse = userDao.getUserByEmail(dto.email());
        if (optionalUserToEndorse.isEmpty()) {
            // Create
            UUID createdId = userDao.createUserFromEmail(dto.email(), dto.email()).orElseThrow(() -> new SoftException("Unable to create user"));
            optionalUserToEndorse = userDao.getUserById(createdId);
        }

        if (optionalUserToEndorse.isEmpty()) {
            throw new RuntimeException("Unable to create user");
        }

        UUID endorsementId = endorsementDao.createEndorsement(
                user.id(),
                optionalUserToEndorse.get().id(),
                dto.message()
        );
        return ResponseEntity.ok(endorsementDao.getEndorsement(endorsementId));
    }

    @DeleteMapping("/endorse/{endorsementId}")
    public ResponseEntity<Void> deleteEndorsement(@PathVariable UUID endorsementId, @CurrentUser UserToken user) {
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        // Check if user is owner of endorsement
        Endorsement endorsement = endorsementDao.getEndorsement(endorsementId);
        if (endorsement == null) {
            return ResponseEntity.notFound().build();
        }

        if (!endorsement.getEndorserId().equals(user.id())) {
            return ResponseEntity.status(403).build();
        }

        endorsementDao.deleteEndorsement(endorsementId);
        return ResponseEntity.ok().build();
    }

}
