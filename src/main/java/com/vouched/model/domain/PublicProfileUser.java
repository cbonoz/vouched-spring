package com.vouched.model.domain;

public record PublicProfileUser(
    String firstName,
    String lastName,
    String bio,
    String title,
    String handle,
    String agreementText
) {

  public static PublicProfileUser fromUser(VouchedUser user) {
    return new PublicProfileUser(
        user.getFirstName(),
        user.getLastName(),
        user.getBio(),
        user.getTitle(),
        user.getHandle(),
        user.getAgreementText()
    );
  }

}
