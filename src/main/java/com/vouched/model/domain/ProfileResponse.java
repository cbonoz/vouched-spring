package com.vouched.model.domain;

import java.util.List;

public record ProfileResponse(
    VouchedUser user,
    List<Endorsement> endorsements
) {

}
