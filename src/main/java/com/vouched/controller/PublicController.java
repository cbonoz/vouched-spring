package com.vouched.controller;

import com.vouched.dao.UserDao;
import com.vouched.dao.endorsement.AccessDao;
import com.vouched.dao.endorsement.EndorsementDao;
import com.vouched.model.domain.Endorsement;
import com.vouched.model.domain.ProfileResponse;
import com.vouched.model.domain.PublicProfileUser;
import com.vouched.model.domain.VouchedUser;
import com.vouched.model.dto.EndorsementAccessRequest;
import com.vouched.model.param.BasicEmailTemplate;
import com.vouched.service.email.EmailService;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
public class PublicController {

  private final UserDao userDao;
  private final EndorsementDao endorsementDao;

  private final AccessDao accessDao;
  private final EmailService emailService;

  @Inject
  public PublicController(UserDao userDao, EndorsementDao endorsementDao,
      AccessDao accessDao, EmailService emailService) {
    this.userDao = userDao;
    this.endorsementDao = endorsementDao;
    this.accessDao = accessDao;
    this.emailService = emailService;
  }

  @GetMapping("/up")
  public ResponseEntity<String> health() {
    return ResponseEntity.ok().body("up");
  }

  @PostMapping("/profile/access")
  public ResponseEntity<String> requestAccess(
      @RequestBody EndorsementAccessRequest endorsementAccessRequest) {

    // get user by handle
    Optional<VouchedUser> handleUserMaybe = userDao.getUserByHandle(
        endorsementAccessRequest.handle());
    if (handleUserMaybe.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    VouchedUser vouchedUser = handleUserMaybe.get();
    accessDao.createEndorserAccess(vouchedUser.getId(), endorsementAccessRequest.email(),
        endorsementAccessRequest.message());

    String emailToNotify = vouchedUser.getEmail();

    BasicEmailTemplate basicEmailTemplate = new BasicEmailTemplate(
        "New Access Request:<br/><br/>" + endorsementAccessRequest.message(),
        "You have a new access request from " + endorsementAccessRequest.email(),
        "usevouched.com/profile?tab=access"
    );

    emailService.sendBasicEmail(emailToNotify, "New Endorsement Access Request",
        basicEmailTemplate);

    return ResponseEntity.ok().body("ok");
  }

  @GetMapping("/profile")
  public ResponseEntity<ProfileResponse> getProfile(
      @RequestParam("handle") String handle,
      @RequestParam("requesterEmail") String requesterEmail
  ) {

    Optional<VouchedUser> handleUserMaybe = userDao.getUserByHandle(handle);
    if (handleUserMaybe.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    // TODO: add access check.
     accessDao.getApprovedEndorserAccessForUser()

    VouchedUser vouchedUser = handleUserMaybe.get();
    List<Endorsement> endorsements = endorsementDao.getEndorsementsForEndorserId(
        vouchedUser.getId(), 1000, 0
    );

    int endorsementCount = endorsements.size(); // todo: paginate

    PublicProfileUser publicProfileUser = PublicProfileUser.fromUser(vouchedUser);

    ProfileResponse profileResponse = new ProfileResponse(
        publicProfileUser, !locked ? endorsements : List.of(), endorsementCount, locked
    );

    return ResponseEntity.ok(profileResponse);
  }


}
