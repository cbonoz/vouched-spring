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
public class UserAuthService {

  private final ClerkService clerkService;
  private final UserDao userDao;

  @Inject
  public UserAuthService(ClerkService clerkService, UserDao userDao) {
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
    Optional<VouchedUser> createdUserMaybe = userDao.getUserByEmail(emailAddress);

    final VouchedUser createdUser;
    if (createdUserMaybe.isEmpty()) {
      UUID id = userDao.createBaseUser(
          userToken.firstName(),
          userToken.lastName(), userToken.imageUrl(), emailAddress,
          userToken.externalId());

      Optional<VouchedUser> userById = userDao.getUserById(id);

      if (userById.isEmpty()) {
        throw new RuntimeException("User not created");
      }
      createdUser = userById.get();
    } else {
      createdUser = createdUserMaybe.get();
    }

    return new UserToken(
        createdUser.getId(),
        userToken.id(),
        emailAddress,
        userToken.imageUrl(),
        userToken.firstName(),
        userToken.lastName(),
        createdUser.getHandle()
    );
  }
}