package com.vouched.controller;

import static java.util.stream.Collectors.toSet;

import com.google.common.collect.Sets;
import com.vouched.annotation.CurrentUser;
import com.vouched.auth.UserToken;
import com.vouched.dao.UserDao;
import com.vouched.dao.endorsement.EndorsementDao;
import com.vouched.error.SoftException;
import com.vouched.model.domain.PublicProfileUser;
import com.vouched.model.domain.VouchedUser;
import com.vouched.model.dto.BasicQueryRequest;
import com.vouched.model.dto.CreateEndorsementDto;
import com.vouched.model.dto.CreateUserDto;
import com.vouched.service.EndorsementService;
import com.vouched.service.UserService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

  // log
  private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);

  private final EndorsementService endorsementService;
  private final UserDao userDao;
  private final EndorsementDao endorsementDao;
  private final UserService userService;

  @Inject
  public AdminController(EndorsementService endorsementService, UserDao userDao,
      EndorsementDao endorsementDao, UserService userService) {
    this.endorsementService = endorsementService;
    this.userDao = userDao;
    this.endorsementDao = endorsementDao;
    this.userService = userService;
  }

  @GetMapping("/up")
  public ResponseEntity<String> health() {
    return ResponseEntity.ok().body("up");
  }

  @GetMapping("/test-exception")
  public ResponseEntity<String> testException(
      @RequestBody BasicQueryRequest request, @CurrentUser UserToken currentUser)
      throws Exception {
    throw new Exception(request.query());
  }

  @PostMapping("/users/upload")
  public ResponseEntity<List<CreateUserDto>> uploadUsers(
      @CurrentUser UserToken currentUser,
      @RequestBody Map<String, PublicProfileUser> emailToUserMap) {
    userService.validateSuperUser(currentUser);

    List<CreateUserDto> users = userService.uploadUsers(emailToUserMap);
    return ResponseEntity.ok(users);
  }

  @PostMapping("/endorsements/upload")
  public ResponseEntity<Set<UUID>> uploadEndorsements(
      @CurrentUser UserToken currentUser,
      @RequestBody Map<String, List<CreateEndorsementDto>> emailToEndorsementsMap) {
    userService.validateSuperUser(currentUser);

    // get emails
    Set<String> emails = emailToEndorsementsMap.keySet().stream().map(String::trim)
        .collect(toSet());
    List<VouchedUser> usersWithEmails = userDao.getUsersWithEmails(emails);
    // check emails
    if (usersWithEmails.size() != emails.size()) {
      Set<String> existingEmails = usersWithEmails.stream().map(VouchedUser::getEmail)
          .map(String::trim)
          .collect(toSet());
      emails.removeAll(existingEmails);
      throw new SoftException("Users do not exist: " + emails);
    }

    Set<UUID> endorsementIds = Sets.newHashSet();
    // iterate over users
    for (VouchedUser user : usersWithEmails) {
      List<CreateEndorsementDto> endorsements = emailToEndorsementsMap.get(
          user.getEmail().trim());
      for (CreateEndorsementDto endorsement : endorsements) {
        endorsementService.validateEndorsement(endorsement);
        try {
          UUID endorsementId = endorsementDao.createEndorsement(user.getId(),
              endorsement.message(),
              endorsement.firstName(), endorsement.lastName(), endorsement.skills(),
              endorsement.relationship());
          endorsementIds.add(endorsementId);
        } catch (Exception e) {
          LOG.warn("Failed to create endorsement for user: " + endorsement,
              e);
        }
      }
    }

    return ResponseEntity.ok(
        endorsementIds
    );
  }
}
