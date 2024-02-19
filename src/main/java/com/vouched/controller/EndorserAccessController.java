package com.vouched.controller;

import com.vouched.annotation.CurrentUser;
import com.vouched.auth.UserToken;
import com.vouched.dao.endorsement.AccessDao;
import com.vouched.model.domain.EndorserAccess;
import com.vouched.model.dto.ActionRequest;
import com.vouched.model.param.BasicEmailTemplate;
import com.vouched.service.email.EmailService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Inject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/endorser/requests")
public class EndorserAccessController {


  private final AccessDao accessDao;
  private final EmailService emailService;

  @Inject
  public EndorserAccessController(AccessDao accessDao, EmailService emailService) {
    this.accessDao = accessDao;
    this.emailService = emailService;
  }

  // get requests for current user
  @GetMapping("/list")
  public ResponseEntity<List<EndorserAccess>> getEndorserAccessForUser(
      @CurrentUser UserToken user) {
    if (user == null) {
      return ResponseEntity.status(401).build();
    }
    return ResponseEntity.ok(accessDao.getEndorserAccessForEndorser(user.id(), 100, 0));
  }


  @PatchMapping("/{requestId}")
  public ResponseEntity<Void> modifyAccess(@CurrentUser UserToken user,
      @PathVariable UUID requestId, @RequestBody ActionRequest actionRequest) {
    if (user == null) {
      return ResponseEntity.status(401).build();
    }
    final Optional<EndorserAccess> endorserAccessMaybe;
    if (actionRequest.action().equals("accept")) {
      endorserAccessMaybe = accessDao.approveEndorserAccess(user.id(), requestId);
    } else if (actionRequest.action().equals("reject")) {
      endorserAccessMaybe = accessDao.deleteEndorserAccess(user.id(), requestId);
    } else {
      return ResponseEntity.badRequest().build();
    }

    if (endorserAccessMaybe.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    EndorserAccess endorserAccess = endorserAccessMaybe.get();

    String actionMessage =
        actionRequest.action().equals("accept") ? "approved" : "rejected";
    String content = String.format("Your request to access %s Vouched list has been %s!",
        user.firstName(), actionMessage);
    BasicEmailTemplate basicEmailTemplate = new BasicEmailTemplate(
        content, "View profile", "usevouched.com/profile/" + user.handle());

    emailService.sendBasicEmail(endorserAccess.getRequesterEmail(),
        "Vouched Access Update (" + actionMessage + ")", basicEmailTemplate);

    return ResponseEntity.ok().build();
  }

}
