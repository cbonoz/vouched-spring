package com.vouched.dao;

import com.hubspot.rosetta.jdbi3.BindWithRosetta;
import com.vouched.model.domain.UpdateVouchedUser;
import com.vouched.model.domain.VouchedUser;
import java.util.Optional;
import java.util.UUID;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface UserDao {

  @SqlQuery("SELECT * FROM users WHERE email = :emailAddress")
  Optional<VouchedUser> getUserByEmail(String emailAddress);

  @SqlUpdate(
      "INSERT INTO users (first_name, last_name, image_url, email, external_id) VALUES (:firstName, :lastName, :imageUrl, :email, :externalId) on conflict do nothing")
  @GetGeneratedKeys
  Optional<UUID> createBaseUser(@Bind String firstName, @Bind String lastName,
      @Bind String imageUrl, @Bind String email, @Bind String externalId);

  @SqlQuery("SELECT * FROM users WHERE id = :id")
  Optional<VouchedUser> getUserById(UUID id);

  // get user by external
  @SqlQuery("SELECT * FROM users WHERE external_id = :externalId")
  Optional<VouchedUser> getUserByExternalId(String externalId);

  @SqlQuery("SELECT * FROM users WHERE handle = :handle")
  Optional<VouchedUser> getUserByHandle(String handle);

  @SqlUpdate("UPDATE users SET first_name = :firstName, last_name = :lastName, image_url = :imageUrl WHERE external_id = :externalId")
  void updateUser(@BindWithRosetta UpdateVouchedUser user);

}
