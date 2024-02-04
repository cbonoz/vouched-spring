package com.vouched.service;


import com.microsoft.kiota.authentication.ApiKeyAuthenticationProvider;
import com.microsoft.kiota.authentication.ApiKeyLocation;
import com.microsoft.kiota.http.OkHttpRequestAdapter;
import com.vouched.config.AppProperties;
import io.github.zzhorizonzz.client.models.User;
import io.github.zzhorizonzz.sdk.ClerkClient;
import io.github.zzhorizonzz.sdk.user.request.UpdateUserRequest;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;

@Component
public class ClerkService {

    private final ClerkClient clerkClient;

    // https://www.baeldung.com/spring-5-webclient
    @Inject
    public ClerkService(AppProperties appProperties) {
        final ApiKeyAuthenticationProvider authProvider = new ApiKeyAuthenticationProvider(appProperties.clerkSecret, "authorization", ApiKeyLocation.HEADER, appProperties.clerkUrl);
        final OkHttpRequestAdapter adapter = new OkHttpRequestAdapter(authProvider);
        this.clerkClient = new ClerkClient(adapter);
    }

    public Optional<User> getClerkUser(String username) {
        return Optional.ofNullable(clerkClient.getUserService().read(username));
//        return webClient.get()
//                .uri("/users/" + username)
//                .header("Authorization", "Bearer " + appProperties.clerkSecret)
//                .retrieve()
//                .bodyToMono(ClerkUser.class)
//                // Log error and return optional empty
//                .blockOptional();
    }

    public User updateUser(String userId, UpdateUserRequest updateUserRequest) {
        return clerkClient.getUserService().update(userId, updateUserRequest);
    }


}
