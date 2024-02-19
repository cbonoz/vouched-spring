package com.vouched.model.dto;

import lombok.Data;

@Data
public class CreateUserDto {

  String email;
  String firstName;
  String lastName;
  String handle;
  String imageUrl;
  String title;
  String bio;
  String agreementText;
  String externalId;


}
