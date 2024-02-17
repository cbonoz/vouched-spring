package com.vouched.controller;

import com.vouched.dao.endorsement.AccessDao;
import com.vouched.service.email.EmailService;
import javax.inject.Inject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/endorser/access")
public class EndorserAccessController {


  private final AccessDao accessDao;
  private final EmailService emailService;

  @Inject
  public EndorserAccessController(AccessDao accessDao, EmailService emailService) {
    this.accessDao = accessDao;
    this.emailService = emailService;
  }

  @PostMapping
  public void requestAccess() {

  }


}
