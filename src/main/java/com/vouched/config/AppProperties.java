package com.vouched.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AppProperties {

    public static final String APP_NAME = "Vouched";
    public static final String SENDER_EMAIL = "noreply@vouched.com";
    public static final int MAX_EDITS_PER_DAY = 30;

    @Value("${app.clerk-url}")
    public String clerkUrl;

    @Value("${app.admin-emails}")
    public List<String> adminEmails;

    @Value("${app.clerk-secret}")
    public String clerkSecret;

    @Value("${app.mailjet-api-key}")
    public String mailjetApiKey;

    @Value("${app.mailjet-secret-key}")
    public String mailjetSecretKey;


}
