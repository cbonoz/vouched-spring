package com.vouched.controller;

import com.vouched.annotation.CurrentUser;
import com.vouched.auth.UserToken;
import com.vouched.dao.UserDao;
import com.vouched.dao.endorsement.AccessDao;
import com.vouched.dao.endorsement.EndorsementDao;
import com.vouched.model.domain.Endorsement;
import com.vouched.model.domain.VouchedUser;
import com.vouched.model.dto.CreateEndorsementDto;
import com.vouched.service.EndorsementService;
import com.vouched.service.email.EmailService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Inject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/endorsements")
public class EndorsementController {

  private final UserDao userDao;
  private final EndorsementDao endorsementDao;
  private final EmailService emailService;
  private final AccessDao accessDao;
  private EndorsementService endorsementService;

  @Inject
  public EndorsementController(UserDao userDao, EndorsementDao endorsementDao,
      EmailService emailService, AccessDao accessDao,
      EndorsementService endorsementService) {
    this.userDao = userDao;
    this.endorsementDao = endorsementDao;
    this.emailService = emailService;
    this.accessDao = accessDao;
    this.endorsementService = endorsementService;
  }

  @GetMapping("/list/handle") // get list of endorsements by handle
  public ResponseEntity<List<Endorsement>> getEndorsementsForHandleWithAccessCheck(
      @RequestParam("handle") String handleMaybe, @RequestParam("limit") int limit,
      @RequestParam("offset") int offset) {
    int newLimit = Math.max(limit == 0 ? 10 : limit, 1000);
    if (handleMaybe == null) {
      return ResponseEntity.notFound().build();
    }

    // TODO: add access check.
//    accessDao.getEndorserAccessForUser()

    Optional<VouchedUser> userIdMaybe = userDao.getUserByHandle(handleMaybe);
    return userIdMaybe.map(vouchedUser -> ResponseEntity.ok(
            endorsementDao.getEndorsementsForEndorserId(vouchedUser.id(), newLimit, offset)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/list")
  // get endorsements for current user
  public ResponseEntity<List<Endorsement>> getEndorsementsForCurrentUser(
      @CurrentUser UserToken user, @RequestParam("limit") int limit,
      @RequestParam("offset") int offset) {
    if (user == null) {
      return ResponseEntity.notFound().build();
    }
    int newLimit = Math.max(limit == 0 ? 10 : limit, 1000);

    return ResponseEntity.ok(
        endorsementDao.getEndorsementsForEndorserId(user.id(), newLimit, offset));
  }


  @PostMapping("")
  public ResponseEntity<Endorsement> endorseUser(@RequestBody CreateEndorsementDto dto,
      @CurrentUser UserToken user) {
    if (user == null) {
      return ResponseEntity.notFound().build();
    }
    endorsementService.validateEndorsement(dto);

    UUID endorsementId = endorsementDao.createEndorsement(
        user.id(),
        dto.message(),
        dto.firstName(),
        dto.lastName(),
        dto.relationship()
    );

    return ResponseEntity.ok(endorsementDao.getEndorsement(endorsementId).orElseThrow());
  }

  @DeleteMapping("/{endorsementId}")
  public ResponseEntity<UUID> deleteEndorsement(@PathVariable UUID endorsementId,
      @CurrentUser UserToken user) {
    if (user == null) {
      return ResponseEntity.notFound().build();
    }
    // Check if user is owner of endorsement
    Optional<Endorsement> endorsementMaybe = endorsementDao.getEndorsement(endorsementId);
    if (endorsementMaybe.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    Endorsement endorsement = endorsementMaybe.get();

    if (!endorsement.getEndorserId().equals(user.id())) {
      return ResponseEntity.status(403).build();
    }

    endorsementDao.deleteEndorsement(endorsementId);
    return ResponseEntity.ok(endorsementId);
  }
}
