package com.vouched.service;


import com.vouched.auth.UserToken;
import com.vouched.dao.UserDao;
import com.vouched.model.domain.ClerkUser;
import com.vouched.model.domain.VouchedUser;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Inject;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
    String emailAddress = userToken.emailAddresses().get(0).emailAddress();

    // If user with email isn't present in DB, create this user.
    VouchedUser createdUser = userDao.getUserByEmail(emailAddress)
        .orElseGet(() -> {
          UUID id = userDao.createBaseUser(emailAddress,
              userToken.firstName(),
              userToken.lastName(), userToken.imageUrl(), userToken.externalId());
          Optional<VouchedUser> userById = userDao.getUserById(id);

          if (userById.isEmpty()) {
            throw new RuntimeException("User not created");
          }
          return userById.get();
        });

    return new UserToken(
        createdUser.id(),
        userToken.externalId(),
        emailAddress,
        userToken.imageUrl(),
        userToken.firstName(),
        userToken.lastName(),
        createdUser.handle(),
        createdUser.activatedAt()
    );
  }
}