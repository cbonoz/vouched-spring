package com.vouched.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
@RequestMapping("/public")
public class PublicController {

    @Inject
    public PublicController() {
    }

    @GetMapping("/up")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok().body("up");
    }
}
