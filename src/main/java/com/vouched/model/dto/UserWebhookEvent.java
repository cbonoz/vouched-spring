package com.vouched.model.dto;

import java.util.List;
import java.util.Map;

public record UserWebhookEvent(
    Data data,
    String object,
    String type
) {

  public record Data(
      String birthday,
      long created_at,
      List<EmailAddress> email_addresses,
      List<Object> external_accounts,
      String external_id,
      String first_name,
      String gender,
      String id,
      String last_name,
      boolean locked,
      long last_sign_in_at,
      String object,
      boolean password_enabled,
      List<Object> phone_numbers,
      String primary_email_address_id,
      String primary_phone_number_id,
      String primary_web3_wallet_id,
      Map<String, Object> private_metadata,
      String profile_image_url,
      Map<String, Object> public_metadata,
      boolean two_factor_enabled,
      Map<String, Object> unsafe_metadata,
      long updated_at,
      String username,
      List<Object> web3_wallets
  ) {

    public record EmailAddress(
        String email_address,
        String id,
        List<Object> linked_to,
        String object,
        Verification verification
    ) {

      public record Verification(
          String status,
          String strategy
      ) {

      }
    }
  }
}