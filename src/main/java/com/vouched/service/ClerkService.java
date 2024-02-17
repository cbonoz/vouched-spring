package com.vouched.service;


import com.vouched.config.AppProperties;
import com.vouched.model.domain.ClerkUser;
import java.util.Optional;
import javax.inject.Inject;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ClerkService {

  private final WebClient webClient;

  private final AppProperties appProperties;

  // https://www.baeldung.com/spring-5-webclient
  @Inject
  public ClerkService(AppProperties appProperties) {
    this.appProperties = appProperties;
    this.webClient = WebClient.builder().baseUrl(appProperties.clerkUrl).build();
  }

  public Optional<ClerkUser> getClerkUser(String username) {
    return webClient.get()
        .uri("/users/" + username)
        .header("Authorization", "Bearer " + appProperties.clerkSecret)
        .retrieve()
        .bodyToMono(ClerkUser.class)
        // Log error and return optional empty
        .blockOptional();
  }


}
