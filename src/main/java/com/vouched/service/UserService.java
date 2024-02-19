package com.vouched.service;

import com.vouched.auth.UserToken;
import com.vouched.config.AppProperties;
import com.vouched.dao.UserDao;
import com.vouched.error.SoftException;
import com.vouched.model.domain.PublicProfileUser;
import com.vouched.model.domain.VouchedUser;
import com.vouched.model.dto.CreateUserDto;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import org.springframework.stereotype.Service;

@Service
public class UserService {


  private final UserDao userDao;
  private final AppProperties appProperties;

  @Inject
  public UserService(UserDao userDao, AppProperties appProperties) {
    this.userDao = userDao;
    this.appProperties = appProperties;
  }

  // TODO: add secret
  public void validateSuperUser(UserToken userToken) {
    if (!userToken.externalId().equals(UserToken.SUPER_USER_EXTERNAL_ID)) {
      throw new SoftException("Super user required");
    }
  }

  public List<PublicProfileUser> getHomePageProfiles() {
    return userDao.getUsersWithEmails(appProperties.homePageEmails)
        .stream()
        .map(PublicProfileUser::fromUser)
        .toList();
  }

  public List<String> uploadUsers(Map<String, PublicProfileUser> emailToUserMap) {
    // check for existing users by email
    Set<String> emails = emailToUserMap.keySet();
    List<VouchedUser> existingUsers = userDao.getUsersWithEmails(emails);
    if (!existingUsers.isEmpty()) {
      throw new SoftException("Users already exist: " + existingUsers);
    }
    List<CreateUserDto> usersToCreate = emailToUserMap.entrySet().stream()
        .map(emailUserEntry -> {
          // Upload image to clerk
          String email = emailUserEntry.getKey();
          PublicProfileUser user = emailUserEntry.getValue();
          String imageUrl = user.imageUrl();
          return createUserDto(user, email, imageUrl);
        }).toList();

    // create new users
    userDao.createUsers(usersToCreate);
    return emails.stream().toList();
  }

  public CreateUserDto createUserDto(PublicProfileUser user, String email,
      String imageUrl) {
    CreateUserDto createUserDto = new CreateUserDto();
    createUserDto.setFirstName(user.firstName());
    createUserDto.setLastName(user.lastName());
    createUserDto.setEmail(email);
    createUserDto.setImageUrl(imageUrl);
    createUserDto.setHandle(user.handle());
    createUserDto.setBio(user.bio());
    createUserDto.setTitle(user.title());
    createUserDto.setAgreementText(user.agreementText());
    createUserDto.setExternalId(null);
    return createUserDto;

  }
}
