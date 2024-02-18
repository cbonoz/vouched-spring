package com.vouched.controller;

import com.vouched.dao.UserDao;
import com.vouched.dao.endorsement.EndorsementDao;
import com.vouched.model.domain.Endorsement;
import com.vouched.model.domain.ProfileResponse;
import com.vouched.model.domain.VouchedUser;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
public class PublicController {

  private final UserDao userDao;
  private final EndorsementDao endorsementDao;

  private final

  @Inject
  public PublicController() {
  }

  @GetMapping("/up")
  public ResponseEntity<String> health() {
    return ResponseEntity.ok().body("up");
  }

  @GetMapping("/profile")
  public ResponseEntity<ProfileResponse> getProfile(
      @RequestParam("handle") String handle
  ) {

    Optional<VouchedUser> handleUserMaybe = userDao.getUserByHandle(handle);
    if (handleUserMaybe.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    // TODO: add access check.
    // accessDao.getEndorserAccessForUser()

    VouchedUser vouchedUser = handleUserMaybe.get();
    List<Endorsement> endorsements = endorsementDao.getEndorsementsForEndorserId(
        vouchedUser.getId(), 1000, 0
    );

    ProfileResponse profileResponse = new ProfileResponse(
        vouchedUser, endorsements
    );

    return ResponseEntity.ok(profileResponse);
  }


}
