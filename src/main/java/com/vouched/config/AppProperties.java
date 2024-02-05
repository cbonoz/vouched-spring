package com.vouched.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {

  public static final String APP_NAME = "Vouched";
  public static final String SENDER_EMAIL = "noreply@vouched.com";

  @Value("${app.domain}")
  public String appDomain;

  @Value("${app.clerk-url}")
  public String clerkUrl;

  @Value("${app.admin-emails}")
  public List<String> adminEmails;

  @Value("${app.clerk-secret}")
  public String clerkSecret;

  @Value("${app.brevo-api-key}")
  public String brevoApiKey;

}
