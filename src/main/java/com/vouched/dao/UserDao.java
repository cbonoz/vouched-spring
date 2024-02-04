package com.vouched.dao;

import com.vouched.model.domain.VouchedUser;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.Optional;
import java.util.UUID;

public interface UserDao {

    @SqlQuery("SELECT * FROM users WHERE email = :emailAddress")
    Optional<VouchedUser> getUserByEmail(String emailAddress);

    @SqlUpdate(
            "INSERT INTO users (first_name, last_name, image_url, email, external_id) VALUES (:firstName, :lastName, :imageUrl, :email, :externalId) on conflict do nothing")
    @GetGeneratedKeys
    Optional<UUID> createBaseUser(@Bind String firstName, @Bind String lastName, @Bind String imageUrl, @Bind String email, @Bind String externalId);

    @SqlQuery("SELECT * FROM users WHERE id = :id")
    Optional<VouchedUser> getUserById(UUID id);

    @SqlQuery("SELECT * FROM users WHERE handle = :handle")
    Optional<VouchedUser> getUserByHandle(String handle);
}
