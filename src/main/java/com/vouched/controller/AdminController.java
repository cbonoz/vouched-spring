package com.vouched.controller;

import com.vouched.annotation.CurrentUser;
import com.vouched.auth.UserToken;
import com.vouched.model.dto.BasicQueryRequest;
import com.vouched.service.EndorsementService;
import javax.inject.Inject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

  private final EndorsementService endorsementService;

  @Inject
  public AdminController(EndorsementService endorsementService) {
    this.endorsementService = endorsementService;
  }

  @GetMapping("/up")
  public ResponseEntity<String> health() {
    return ResponseEntity.ok().body("up");
  }

  @GetMapping("/test-exception")
  public ResponseEntity<String> testException(
      @RequestBody BasicQueryRequest request, @CurrentUser UserToken currentUser) throws Exception {
    throw new Exception(request.query());
  }
}
