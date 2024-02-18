package com.vouched.model.domain;

import java.util.List;

public record ProfileResponse(
    PublicProfileUser user,
    List<Endorsement> endorsements,
    int endorsementCount,
    boolean locked,
    boolean yourPage

) {

}
