package com.vouched.service;


import com.vouched.auth.UserToken;
import com.vouched.dao.UserDao;
import com.vouched.model.domain.VouchedUser;
import io.github.zzhorizonzz.client.models.EmailAddress;
import io.github.zzhorizonzz.client.models.User;
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

        Optional<User> userTokenOptional = clerkService.getClerkUser(username);
        if (userTokenOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        User userToken = userTokenOptional.get();
        EmailAddress emailEntry = userToken.getEmailAddresses().get(0);

        // If user with email isn't present in DB, create this user.
        VouchedUser createdVouchedUser = userDao.getUserByEmail(emailEntry.getEmailAddress())
                .orElseGet(() -> {
                    UUID id = userDao.createBaseUser(userToken.getFirstName(), userToken.getLastName(), userToken.getImageUrl(), emailEntry.getEmailAddress(), username).orElseThrow();
                    return userDao.getUserById(id).orElseThrow();
                });


        return new UserToken(
                createdVouchedUser.id(),
                emailEntry.getEmailAddress(),
                userToken.getImageUrl(),
                userToken.getUsername(),
                userToken.getFirstName(),
                userToken.getLastName(),
                createdVouchedUser.handle(),
                createdVouchedUser.activatedAt(),
                userToken.getPrivateMetadata().getAdditionalData()
        );
    }
}
