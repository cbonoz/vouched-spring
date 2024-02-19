package com.vouched.controller;

import com.vouched.annotation.CurrentUser;
import com.vouched.auth.UserToken;
import com.vouched.dao.UserDao;
import com.vouched.dao.endorsement.AccessDao;
import com.vouched.dao.endorsement.EndorsementDao;
import com.vouched.model.domain.Endorsement;
import com.vouched.model.domain.EndorserAccess;
import com.vouched.model.domain.ProfileResponse;
import com.vouched.model.domain.PublicProfileUser;
import com.vouched.model.domain.VouchedUser;
import com.vouched.model.dto.EndorsementAccessRequest;
import com.vouched.model.param.BasicEmailTemplate;
import com.vouched.service.UserInputService;
import com.vouched.service.email.EmailService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
  private final UserInputService userInputService;

  @Inject
  public PublicController(UserDao userDao, EndorsementDao endorsementDao,
      AccessDao accessDao, EmailService emailService, UserInputService userInputService) {
    this.userDao = userDao;
    this.endorsementDao = endorsementDao;
    this.accessDao = accessDao;
    this.emailService = emailService;
    this.userInputService = userInputService;
  }

  @GetMapping("/up")
  public ResponseEntity<String> health() {
    return ResponseEntity.ok().body("up");
  }

  @PostMapping("/profile/access")
  public ResponseEntity<EndorserAccess> requestAccess(
      @RequestBody EndorsementAccessRequest endorsementAccessRequest) {

    // get user by handle
    Optional<VouchedUser> handleUserMaybe = userDao.getUserByHandle(
        endorsementAccessRequest.handle());
    if (handleUserMaybe.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    VouchedUser vouchedUser = handleUserMaybe.get();
    UUID accessId = accessDao.createEndorserAccess(vouchedUser.getId(),
        endorsementAccessRequest.email(),
        endorsementAccessRequest.message());

    String emailToNotify = vouchedUser.getEmail();

    BasicEmailTemplate basicEmailTemplate = new BasicEmailTemplate(
        "New Access Request from " + endorsementAccessRequest.email().toLowerCase()
            + ":<br/><br/>" + endorsementAccessRequest.message(),
        "Accept/reject request",
        "usevouched.com/profile?tab=access"
    );

    emailService.sendBasicEmail(emailToNotify, "New Endorsement Access Request",
        basicEmailTemplate);

    return ResponseEntity.ok(accessDao.getEndorsementById(accessId));
  }

  @GetMapping("/profile")
  public ResponseEntity<ProfileResponse> getProfilePage(
      @CurrentUser UserToken user,
      @RequestParam("handle") String handle,
      @RequestParam("requesterEmail") Optional<String> requesterEmail
  ) {

    Optional<VouchedUser> handleUserMaybe = userDao.getUserByHandle(handle);
    if (handleUserMaybe.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    VouchedUser vouchedUser = handleUserMaybe.get();

    if (vouchedUser.getActivatedAt() == 0) {
      return ResponseEntity.notFound().build();
    }

    final boolean locked;
    boolean yourPage = false;

    if (user != null && user.id().equals(vouchedUser.getId())) {
      locked = false;
      yourPage = true;
    } else if (requesterEmail.isEmpty() || requesterEmail.get().isBlank()) {
      locked = true;
    } else {
      userInputService.validateEmail(requesterEmail.get());

      Optional<EndorserAccess> endorserAccessMaybe = accessDao.getEndorserAccess(
          vouchedUser.getId(), requesterEmail.get());
      locked = endorserAccessMaybe.isEmpty()
          || endorserAccessMaybe.get().getApprovedAt() == null;
    }

    List<Endorsement> endorsements = endorsementDao.getEndorsementsForEndorserId(
        vouchedUser.getId(), 1000, 0
    );

    int endorsementCount = endorsements.size(); // todo: paginate

    PublicProfileUser publicProfileUser = PublicProfileUser.fromUser(vouchedUser);

    ProfileResponse profileResponse = new ProfileResponse(
        publicProfileUser, !locked ? endorsements : List.of(), endorsementCount, locked,
        yourPage
    );

    return ResponseEntity.ok(profileResponse);
  }


}
