package com.vouched.dao;

import com.hubspot.rosetta.jdbi3.BindWithRosetta;
import com.vouched.model.domain.ClerkUpdateUserRequest;
import com.vouched.model.domain.UpdateUserRequest;
import com.vouched.model.domain.VouchedUser;
import com.vouched.model.dto.CreateUserDto;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

// rosetta

public interface UserDao {

  @SqlQuery("SELECT * FROM users WHERE email = :email")
  Optional<VouchedUser> getUserByEmail(@Bind String email);

  @SqlQuery("SELECT * FROM users")
  List<VouchedUser> getUsers();

  @SqlUpdate(
      "INSERT INTO users(first_name, last_name, image_url, email, external_id) VALUES (:firstName, :lastName, :imageUrl, :email, :externalId) returning *")
  @GetGeneratedKeys
  UUID createBaseUser(@Bind("firstName") String firstName,
      @Bind("lastName") String lastName,
      @Bind("imageUrl") String imageUrl, @Bind("email") String email,
      @Bind("externalId") String externalId);

  @SqlQuery("SELECT * FROM users WHERE id = :id")
  Optional<VouchedUser> getUserById(@Bind UUID id);

  // get user by external
  @SqlQuery("SELECT * FROM users WHERE external_id = :externalId")
  Optional<VouchedUser> getUserByExternalId(@Bind String externalId);

  @SqlQuery("SELECT * FROM users WHERE handle = :handle")
  Optional<VouchedUser> getUserByHandle(@Bind String handle);

  @SqlUpdate("UPDATE users SET first_name = :firstName, last_name = :lastName, image_url = :imageUrl WHERE external_id = :externalId")
  void updateUser(@BindWithRosetta ClerkUpdateUserRequest user);

  @SqlUpdate("UPDATE users SET handle = :handle, first_name = :firstName, last_name = :lastName, image_url = :imageUrl, title = :title, bio = :bio, agreement_text = :agreementText, activated_at = :activatedAt, external_id := externalId WHERE id = :id::uuid")
  void updateUser(@BindWithRosetta UpdateUserRequest user);

  @SqlQuery("SELECT * FROM users WHERE email = ANY(:homePageEmails)")
  List<VouchedUser> getUsersWithEmails(Collection<String> homePageEmails);

  @SqlUpdate("INSERT INTO users(first_name, last_name, handle, title, bio, agreement_text image_url, email, external_id) VALUES (:firstName, :lastName, :handle, :title, :bio, :agreementText, :imageUrl, :email, :externalId)")
  void createUsers(Collection<CreateUserDto> values);
}
