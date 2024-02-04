package com.vouched.model.dto;

import com.vouched.auth.UserToken;
import com.vouched.model.domain.Endorsement;
import java.util.List;

public record ProfileResponse(UserToken user, List<Endorsement> endorsements) {}
