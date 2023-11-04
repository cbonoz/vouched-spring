package com.vouched.controller;

import com.vouched.auth.UserToken;
import com.vouched.dao.EndorsementDao;
import com.vouched.dao.UserDao;
import com.vouched.model.domain.Endorsement;
import com.vouched.model.domain.User;
import com.vouched.model.dto.ProfileResponse;
import com.vouched.service.CustomUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/public")
public class PublicController {

    private final UserDao userDao;
    private final EndorsementDao endorsementDao;
    private final CustomUserService customUserService;

    @Inject
    public PublicController(UserDao userDao, EndorsementDao endorsementDao, CustomUserService customUserService) {
        this.userDao = userDao;
        this.endorsementDao = endorsementDao;
        this.customUserService = customUserService;
    }

    @GetMapping("/up")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok().body("up");
    }

    // Get profile response
    @GetMapping("/profile/{handle}")
    public ResponseEntity<ProfileResponse> getEndorsements(@PathVariable("handle") String handle) {
        Optional<User> userOptional = userDao.getUserByHandle(handle);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        final User user = userOptional.get();
        UserToken userToken = customUserService.loadUserByUsername(user.externalId());
        // TODO: paginate
        List<Endorsement> endorsements = endorsementDao.getEndorsementsForUser(user.id(), 100, 0);
        return ResponseEntity.ok(new ProfileResponse(userToken, endorsements));
    }

}
