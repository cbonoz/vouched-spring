package com.vouched.controller;

import com.vouched.annotation.CurrentUser;
import com.vouched.auth.UserToken;
import com.vouched.dao.EndorsementDao;
import com.vouched.dao.UserDao;
import com.vouched.error.SoftException;
import com.vouched.model.domain.Endorsement;
import com.vouched.model.domain.VouchedUser;
import com.vouched.model.dto.CreateEndorsementDto;
import com.vouched.service.EndorsementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/endorse")
public class EndorsementController {

    private final UserDao userDao;
    private final EndorsementDao endorsementDao;
    private EndorsementService endorsementService;

    @Inject
    public EndorsementController(UserDao userDao, EndorsementDao endorsementDao, EndorsementService endorsementService) {
        this.userDao = userDao;
        this.endorsementDao = endorsementDao;
        this.endorsementService = endorsementService;
    }

    @GetMapping // get list of endorsements by handle
    public ResponseEntity<List<Endorsement>> getEndorsements(@RequestParam("handle") String handle, @RequestParam("limit") int limit, @RequestParam("offset") int offset,
                                                             @RequestParam("requestUserId") UUID requestUserId) {
        Optional<VouchedUser> optionalUser = userDao.getUserByHandle(handle);
        int newLimit = Math.max(limit == 0 ? 10 : limit, 1000);

        if (optionalUser.isEmpty()) {
            throw new SoftException("User not found, make sure the handle is correct: " + handle);
        }

        // TODO: This is a hack to allow users to see all endorsements for themselves (should check token instead).
        boolean includeAll = requestUserId != null && requestUserId.equals(optionalUser.get().id());

        return optionalUser.map(vouchedUser -> ResponseEntity
                        .ok(endorsementDao.getEndorsementsForUser(vouchedUser.id(), newLimit, offset, includeAll)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Approve or reject endorsement
    @PatchMapping("/{endorsementId}")
    public ResponseEntity<Endorsement> approveEndorsement(@PathVariable UUID endorsementId, @RequestParam("approve") boolean approve, @CurrentUser UserToken user) {
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        Optional<Endorsement> endorsementMaybe = endorsementDao.getEndorsement(endorsementId);
        if (endorsementMaybe.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Endorsement endorsement = endorsementMaybe.get();
        if (!endorsement.getUserId().equals(user.id())) {
            return ResponseEntity.status(403).build();
        }

        if (approve) {
            endorsementDao.approveEndorsement(endorsementId);
        } else {
            endorsementDao.deleteEndorsement(endorsementId);
        }
        return ResponseEntity.ok(endorsementDao.getEndorsement(endorsementId).orElseThrow());
    }

    @PostMapping("")
    public ResponseEntity<Endorsement> endorseUser(@RequestBody CreateEndorsementDto dto, @CurrentUser UserToken user) {
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        Optional<VouchedUser> optionalUserToEndorse = userDao.getUserByHandle(dto.handle());

        if (optionalUserToEndorse.isEmpty()) {
            throw new SoftException("User not found, make sure the handle is correct: " + dto.handle());
        }

        endorsementService.validateComment(dto.message());

        UUID endorsementId = endorsementDao.createEndorsement(
                user.id(),
                optionalUserToEndorse.get().id(),
                dto.message()
        );
        return ResponseEntity.ok(endorsementDao.getEndorsement(endorsementId).orElseThrow());
    }

    @DeleteMapping("/{endorsementId}")
    public ResponseEntity<Void> deleteEndorsement(@PathVariable UUID endorsementId, @CurrentUser UserToken user) {
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        // Check if user is owner of endorsement
        Optional<Endorsement> endorsementMaybe = endorsementDao.getEndorsement(endorsementId);
        if (endorsementMaybe.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Endorsement endorsement = endorsementMaybe.get();

        if (!endorsement.getUserId().equals(user.id()) && !endorsement.getEndorserId().equals(user.id())) {
            return ResponseEntity.status(403).build();
        }

        endorsementDao.deleteEndorsement(endorsementId);
        return ResponseEntity.ok().build();
    }
}
