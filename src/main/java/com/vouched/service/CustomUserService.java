package com.vouched.service;


import com.vouched.auth.UserToken;
import com.vouched.dao.UserDao;
import com.vouched.model.domain.EmailEntry;
import com.vouched.model.domain.ClerkUser;
import com.vouched.model.domain.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomUserService {

    private final ClerkService clerkService;
    private final UserDao userDao;

    @Inject
    public CustomUserService(ClerkService clerkService, UserDao userDao) {
        this.clerkService = clerkService;
        this.userDao = userDao;
    }

    public UserToken loadUserByUsername(String username) throws UsernameNotFoundException {
        // Load user information from your data source based on the username
        // Create a UserDetails object and return it
        // Example:

        Optional<ClerkUser> userTokenOptional = clerkService.getClerkUser(username);
        if (userTokenOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        ClerkUser userToken = userTokenOptional.get();
        EmailEntry emailEntry = userToken.emailAddresses().get(0);

        // If user with email isn't present in DB, create this user.
        User createdUser = userDao.getUserByEmail(emailEntry.emailAddress())
                .orElseGet(() -> {
                    UUID id = userDao.createUserFromEmail(emailEntry.emailAddress(), username).orElseThrow();
                    return userDao.getUserById(id).orElseThrow();
                });


        return new UserToken(
                createdUser.id(),
                emailEntry.emailAddress(),
                userToken.imageUrl(),
                userToken.externalId(),
                userToken.firstName() + " " + userToken.lastName(),
                createdUser.handle(),
                createdUser.activatedAt(),
                userToken.privateMetadata()
        );
    }
}
