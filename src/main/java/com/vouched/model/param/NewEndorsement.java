package com.vouched.model.param;

public record NewEndorsement(
    String receiverName,
    String endorserName,
    String endorsement,
    String handle) {

}
