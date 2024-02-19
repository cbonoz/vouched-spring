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
import java.util.stream.Collectors;
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

  public List<CreateUserDto> uploadUsers(Map<String, PublicProfileUser> emailToUserMap) {
    // check for existing users by email
    Set<String> emails = emailToUserMap.keySet();
    List<VouchedUser> existingUsers = userDao.getUsersWithEmails(emails);

    Set<String> existingEmails = existingUsers.stream()
        .map(VouchedUser::getEmail)
        .collect(Collectors.toSet());

    List<CreateUserDto> usersToCreate = emailToUserMap.entrySet().stream()
        .filter(emailUserEntry -> !existingEmails.contains(emailUserEntry.getKey()))
        .map(emailUserEntry -> {
          // Upload image to clerk
          String email = emailUserEntry.getKey();
          PublicProfileUser user = emailUserEntry.getValue();
          String imageUrl = user.imageUrl();
          return createUserDto(user, email, imageUrl);
        }).toList();

    // create new users one by one
    usersToCreate.forEach(userDao::insertUser);

    return usersToCreate;
  }

  public CreateUserDto createUserDto(PublicProfileUser user, String email,
      String imageUrl) {
    CreateUserDto createUserDto = new CreateUserDto();
    createUserDto.setFirstName(user.firstName().trim());
    createUserDto.setLastName(user.lastName().trim());
    createUserDto.setEmail(email.trim());
    createUserDto.setImageUrl(imageUrl.trim());
    // make handle
    String handle = user.handle() != null ? user.handle() : String.format("%s-%s",
        user.firstName().toLowerCase().trim(), user.lastName().toLowerCase().trim());
    createUserDto.setHandle(handle);
    createUserDto.setBio(user.bio().trim());
    createUserDto.setTitle(user.title().trim());
    createUserDto.setAgreementText(user.agreementText());
    createUserDto.setExternalId(null);
    return createUserDto;

  }
}
